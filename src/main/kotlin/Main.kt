@file:Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

import java.io.File

fun main(args: Array<String>) {
    
    if (args.isEmpty()) {
        // Use with IDE
        rearrange(File("E:\\test1"), File("E:\\test2"))
        return
    }
    when (args[0]) {
        "tidy" -> {
            if (args.size < 3) {
                println("请传入输入输出目录")
                return
            }
            tidyUp(File(args[1]), File(args[2]))
        }
        "reset" -> {
            if (args.size < 2) {
                println("请传入目录")
                return
            }
            resetName(File(args[1]))
        }
        "rearrange" -> {
            if (args.size < 3) {
                println("请传入输入输出目录")
                return
            }
            rearrange(File(args[1]), File(args[2]))
            tidyUp(File(args[1]), File(args[2]))
        }
        "addExtension" -> {
            addExtension(File(args[1]), args[2])
        }
    }
}

fun tidyUp(origin: File, target: File) {
    var list: Array<File>
    if (target.listFiles().isNotEmpty() && target.listFiles().last().listFiles().size < 500) {
        println("进入目录: ${target.listFiles().last().absolutePath}")
        list = origin.listFiles()!!
        val tmp = target.listFiles().last().listFiles().size
        for (i in tmp until 500) {
            if (list.size == i - tmp) {
                return
            }
            list[i - tmp].apply {
                copyTo(File(target.listFiles().last(), getName(i, extension)))
                println("${getNum(i)}: " + name + "\t\t-> " +  getName(i, extension))
                delete()
            }
        }
        println("====================")
        println()
    }
    // 刷新
    list = origin.listFiles()!!
    var tmp: File
    val rootSize = target.listFiles().size
    for (i in rootSize until (list.size / 500) + rootSize) {
        tmp = File(target, getNum(i)).apply { mkdirs() }
        println("创建目录: ${tmp.absolutePath}")
        for (j in 0 until 500) {
            list[j + ((i - rootSize) * 500)].apply {
                copyTo(File(tmp, getName(j, extension)))
                println("${getNum(j)}: " + name + "\t\t-> " + getName(j, extension))
                delete()
            }
        }
        println("====================")
        println()
    }
    
    tmp = File(target, getNum(target.listFiles().size)).apply { mkdirs() }
    println("创建目录: ${tmp.absolutePath}")
    // 刷新
    list = origin.listFiles()!!
    for (i in list.indices) {
        list[i].apply {
            copyTo(File(tmp, getName(i, extension)))
            println("${getNum(i)}: " + name + "\t\t-> " + getName(i, extension))
            delete()
        }
    }
    println("====================")
}

fun resetName(file: File) {
    file.listFiles().also { list ->
        if (list.isNullOrEmpty()) {
            return
        }
        
        for (i in list.indices) {
            if (list[i].name == getName(i, list[i].extension)) {
                println(getNum(i) + ": " + list[i].name + ".......通过")
                continue
            }
        
            list[i].apply {
                copyTo(File(file, getName(i, extension)))
                println(getNum(i) + ": " + list[i].name + " -> " + getName(i, extension))
                delete()
            }
        
        }
    }
}

fun addExtension(input: File, ext: String) {
    val list = input.listFiles()
    list.forEach {
        it.renameTo(File(input.absolutePath + ".$ext"))
    }
}

fun rearrange(origin: File, target: File) {
    val targetList = target.listFiles()
    for (i in targetList.indices) {
        val list = targetList[i].listFiles()
        for (j in list.indices) {
            list[j].apply {
                copyTo(File(origin, targetList[i].name + '_' + getNum(j) + ".$extension"))
                println(name + " -> " + targetList[i].name + '_' + getNum(j) + ".$extension")
                delete()
            }
        }
        println(target.listFiles()[i].name + ".......删除")
        targetList[i].delete()
    }
    target.delete()
    target.mkdirs()
    println("重置完成, 进入重新分配")
    println("====================")
    println()
}

fun getNum(num: Int) = if (num < 10) "00$num" else if (num < 100) "0$num" else "$num"

fun getName(num: Int, ext: String) = getNum(num).plus(".$ext")
