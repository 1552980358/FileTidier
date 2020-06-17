@file:Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

import java.io.File
import java.net.URL
import java.util.ArrayList
import javax.sql.rowset.spi.SyncResolver

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
            print("请输入目标: ")
            reset(File(System.`in`.bufferedReader().readLine()))
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
        "downloadTGGraph" -> {
            if (args.size < 2) {
                print("请传入输出目录")
                return
            }
            var link: String
            while (true) {
                print("请输入链接: ")
                link = System.`in`.bufferedReader().readLine()
                if (link.isEmpty()) {
                    break
                }
                downloadTGGraph(URL(link), File(args[1]))
                tidyUp(File(args[1]), File(args[2]))
            }
        }
    }
    
    printComplete()
}

private fun tidyUp(origin: File, target: File) {
    var list: Array<File>
    if (target.listFiles().isNotEmpty() && target.listFiles().last().listFiles().size < 500) {
        println("[IN]进入目录: ${target.listFiles().last().absolutePath}")
        list = origin.listFiles()!!
        val tmp = target.listFiles().last().listFiles().size
        for (i in tmp until 500) {
            if (list.size == i - tmp) {
                break
            }
            list[i - tmp].apply {
                copyTo(File(target.listFiles().last(), getName(i, extension)))
                println("[IN]${getNum(i)}: " + name + " -> " + getName(i, extension))
                delete()
                println("[OK]已删除: ${getName(i, extension)}")
            }
        }
    }
    // 刷新
    list = origin.listFiles()!!
    if (list.isEmpty()) {
        return
    }
    printHyphen()
    var tmp: File
    val rootSize = target.listFiles().size
    for (i in rootSize until (list.size / 500) + rootSize) {
        tmp = File(target, getNum(i)).apply { mkdirs() }
        println("[OK]创建目录: ${tmp.absolutePath}")
        for (j in 0 until 500) {
            list[j + ((i - rootSize) * 500)].apply {
                copyTo(File(tmp, getName(j, extension)))
                println("[IN]${getNum(j)}: " + name + " -> " + getName(j, extension))
                delete()
                println("[OK]已删除: $absolutePath")
            }
        }
    }
    
    // 刷新
    list = origin.listFiles()!!
    if (list.isEmpty()) {
        return
    }
    printHyphen()
    tmp = File(target, getNum(target.listFiles().size)).apply { mkdirs() }
    println("[OK]创建目录: ${tmp.absolutePath}")
    // 刷新
    list = origin.listFiles()!!
    for (i in list.indices) {
        list[i].apply {
            copyTo(File(tmp, getName(i, extension)))
            println("[IN]${getNum(i)}: " + name + " -> " + getName(i, extension))
            delete()
            println("[OK]已删除: ${getName(i, extension)}")
        }
    }
    
}

private fun reset(file: File) {
    file.listFiles().also { list ->
        if (list.isNullOrEmpty()) {
            return
        }
        
        for (i in list.indices) {
            if (list[i].name == getName(i, list[i].extension)) {
                println("[OK]通过: " + list[i].name)
                continue
            }
            
            list[i].apply {
                copyTo(File(file, getName(i, extension)))
                println("[IN]" + name + " -> " + getName(i, extension))
                delete()
                println("[OK]已删除: $absolutePath")
            }
            
        }
    }
    
}

private fun addExtension(input: File, ext: String) {
    val list = input.listFiles()
    list.forEach {
        it.renameTo(File(input.absolutePath + ".$ext"))
    }
}

private fun rearrange(origin: File, target: File) {
    val targetList = target.listFiles()
    var list: Array<File>?
    for (i in targetList.indices) {
        list = targetList[i].listFiles()
        if (list.size == 500) {
            continue
        }
        if (list.isEmpty()) {
            targetList[i].delete()
            continue
        }
        for (j in list.indices) {
            list[j].apply {
                copyTo(File(origin, targetList[i].name + '_' + getNum(j) + ".$extension"))
                println("[IN]" + name + " -> " + targetList[i].name + '_' + getNum(j) + ".$extension")
                delete()
                println("[OK]已删除: $absolutePath")
            }
        }
        println("[OK]已删除: " + targetList[i].absolutePath)
        targetList[i].delete()
    }
    target.delete()
    target.mkdirs()
    println("[OK]重置完成, 进入重新分配")
    printHyphen()
}

private fun downloadTGGraph(url: URL, target: File) {
    val link = ArrayList<String>()
    val tmpString = StringBuilder()
    var start: Boolean
    url.openConnection().getInputStream().use { `is` ->
        `is`.bufferedReader().use { br ->
            br.readText().run {
                substring(
                    indexOf("<article id=\"_tl_editor\" class=\"tl_article_content\">"), indexOf("</article>")
                ).run {
                    substring(indexOf("<figure>"), lastIndexOf("</figure>") + 9)
                        .split("<figure>")
                        .map { map -> map.trim() }
                        .forEach { line ->
                            tmpString.clear()
                            start = false
                            for (it in line) {
                                if (it == '"') {
                                    if (tmpString.isEmpty() && !start) {
                                        start = true
                                        continue
                                    } else if (start) {
                                        break
                                    }
                                }
                                if (start) {
                                    tmpString.append(it)
                                }
                            }
                            if (tmpString.isNotEmpty()) {
                                link.add(tmpString.toString())
                            }
                        }
                }
            }
        }
    }
    link.forEach { line ->
        println("[IN]${line.substring(line.lastIndexOf(if (line.contains("\\")) "\\" else "/") + 1)}")
        File(target, line.substring(line.lastIndexOf(if (line.contains("\\")) "\\" else "/") + 1))
            .apply {
                createNewFile()
                writeBytes(URL("https://telegra.ph${line}").readBytes())
                println("[OK]${absolutePath}")
            }
    }
    println("下载完成")
    printHyphen()
}

private fun printHyphen() = println("--------------------")

private fun printComplete() {
    printHyphen()
    println("    程序执行完毕")
    printHyphen()
    println()
}

private fun getNum(num: Int) = if (num < 10) "00$num" else if (num < 100) "0$num" else "$num"

private fun getName(num: Int, ext: String) = getNum(num).plus(".$ext")
