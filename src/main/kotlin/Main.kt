import java.io.File

fun main(args: Array<String>) {
    
    if (args.isEmpty()) {
        // Use with IDE
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
    }
}

fun tidyUp(origin: File, target: File) {
    var list: Array<File>
    if (target.listFiles().last().listFiles().size < 200) {
        println("进入目录: ${target.listFiles().last().absolutePath}")
        list = origin.listFiles()!!
        val tmp = target.listFiles().last().listFiles().size
        for (i in tmp until 200) {
            if (list.size == i) {
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
    for (i in rootSize until (list.size / 200) + rootSize) {
        tmp = File(target, getNum(i)).apply { mkdirs() }
        println("创建目录: ${tmp.absolutePath}")
        for (j in 0 until 200) {
            list[j + ((i - rootSize) * 200)].apply {
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
    for (i in 0 .. list.lastIndex) {
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
        
        for (i in 0 .. list.lastIndex) {
            if (list[i].name == getName(i, list[i].extension)) {
                println(getNum(i) + ": " + list[i].name + ".......Pass")
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

fun getNum(num: Int) = if (num < 10) "00$num" else if (num < 100) "0$num" else "$num"

fun getName(num: Int, ext: String) = getNum(num).plus(".$ext")