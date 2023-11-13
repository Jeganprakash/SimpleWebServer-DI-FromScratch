package com.jan.dev.framework.factory

import com.jan.dev.framework.annotation.Injected
import com.jan.dev.framework.annotation.SingletonObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.WildcardType
import java.net.JarURLConnection
import java.net.URL
import java.util.concurrent.ConcurrentHashMap

object SingletonObjectsFactory {
    private val allClassesInCurrentPackage = mutableListOf<Class<*>>()
    private val singletonClasses = mutableListOf<Class<*>>()
    private val singletonObjects: ConcurrentHashMap<Class<*>, Any> = ConcurrentHashMap()

    fun initiateSingletonObjects(packageName: String) {
        scanPackage(packageName)
        filterClassesWithSingleTonAnnotation()
        instantiateAndRegisterSingletonClasses()
        injectDependencies()
    }

    fun getObject(key: Class<*>): Any {
        return singletonObjects[key]!!
    }

    private fun getObjects(key: Class<*>): List<Any> {
        val result = mutableListOf<Any>()
        singletonObjects.forEach {
            if (key.isAssignableFrom(it.key)) {
                result.add(it.value)
            }
        }
        return result
    }

    private fun registerObject(key: Class<*>, instance: Any) {
        singletonObjects[key] = instance
    }

    private fun scanPackage(packageName: String) {
        val classLoader = Thread.currentThread().contextClassLoader
        val path = packageName.replace(".", "/")
        val resources = classLoader.getResources(path)

        while (resources.hasMoreElements()) {
            val resource = resources.nextElement()
            if (resource.protocol == "jar") {
                readClassesFromJAr(resource,path)
                break
            } else {
                val file = File(resource.file)
                if (file.isDirectory) {
                    val classFiles = mutableListOf<File>()
                    findClassFiles(file, classFiles)
                    classFiles.forEach {
                        val className = it.toClassName(packageName)
                        val clazz = Class.forName(className)
                        allClassesInCurrentPackage.add(clazz)
                    }
                }
            }
        }
        println("scanned classes Count ${allClassesInCurrentPackage.size}")
    }

    private fun filterClassesWithSingleTonAnnotation() {
        allClassesInCurrentPackage.forEach { clazz ->
            val singletonAnnotatedClass = clazz.getAnnotation(SingletonObject::class.java)
            if (singletonAnnotatedClass != null) {
                singletonClasses.add(clazz)
            }
        }
    }

    private fun instantiateAndRegisterSingletonClasses() {
        singletonClasses.forEach { clazz ->
            // TODO:: COMMENT WHY FIRST CONSTRUCTOR OF ARRAY
            val instance = clazz.constructors[0].newInstance()
            registerObject(instance::class.java, instance)
        }
    }

    private fun injectDependencies() {
        singletonObjects.forEach { (clazz, obj) ->
            setInjectedAnnotatedFields(obj)
        }
    }

    private fun setInjectedAnnotatedFields(obj: Any) {

        val fields = obj.javaClass.declaredFields.filter {
            it.isAnnotationPresent(Injected::class.java)
        }

        fields.forEach {
            val result = if (List::class.java.isAssignableFrom(it.type)) {
                val listType = it.genericType as ParameterizedType
                val typeArgument = listType.actualTypeArguments[0]
                val type = if (typeArgument is WildcardType) {
                    typeArgument.upperBounds[0] as Class<*>
                } else {
                    typeArgument as Class<*>
                }
                getObjects(type)
            } else {
                // TODO handle parents type
                val clazz = it.type
                getObject(clazz)
            }
            it.trySetAccessible()
            it.set(obj, result)
        }
    }

    private fun findClassFiles(directory: File, classFiles: MutableList<File>) {
        val files = directory.listFiles()
        files?.forEach {
            if (it.isDirectory) {
                findClassFiles(it, classFiles)
            } else if (it.name.endsWith(".class")) {
                classFiles.add(it)
            }
        }
    }

    private fun File.toClassName(basePackage: String): String {
        val relativePath = this.absolutePath.removePrefix("/").replace("/", ".").substringAfter(basePackage)
        val className = relativePath.removeSuffix(".class")
        return "$basePackage$className"
    }

    fun getObjects(annotation: Class<out Annotation>): Collection<Any> {
        return singletonObjects.values.filter { obj ->
            obj.javaClass.getAnnotation(annotation) != null
        }
    }

    private fun readClassesFromJAr(res: URL,targetPackagePath:String) {
        try {
            val connection = res.openConnection() as JarURLConnection
            val jarFile = connection.jarFile
            val entries = jarFile.entries()
            while (entries.hasMoreElements()) {
                val jarEntry = entries.nextElement()
                if (jarEntry.name.endsWith(".class") && jarEntry.name.startsWith(targetPackagePath.replace(".", "/"))) {
                    val className = jarEntry.name
                        .replace("/", ".")
                        .replace(".class", "")
                    val clazz = Class.forName(className)
                    allClassesInCurrentPackage.add(clazz)
                }
            }

        } catch (ex: IOException) {
            ex.printStackTrace()
        }

    }
}
