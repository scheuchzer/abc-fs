package org.scheuchzer.abcfs

import co.paralleluniverse.javafs.JavaFS
import java.net.URI
import java.nio.file.FileSystems
import java.nio.file.Paths
import java.util.*


fun main(args: Array<String>) {
    println(Class.forName("org.scheuchzer.abcfs.AbcFileSystemProvider"))
    try {
        if (args.size < 1 || args.size > 2)
            throw IllegalArgumentException()

        var i = 0
        val mountPoint = args[i++]
        val source = args[i++]
        var root = Paths.get(source).toAbsolutePath()
        println("root=${root}")
        val fs =  FileSystems.newFileSystem(URI("abc", root.toAbsolutePath().toString(), null, null), mutableMapOf<String, Object>())



        println("========================")
        println("Mounting filesystem " + fs + " at " + mountPoint )
        println("========================")

        val options = HashMap<String, String>()
        options.put("fsname", fs.javaClass.getSimpleName() + "@" + System.currentTimeMillis())

        JavaFS.mount(fs, Paths.get(mountPoint), true, true, options)
        Thread.sleep(java.lang.Long.MAX_VALUE)
    } catch (e: IllegalArgumentException) {
        System.err.println("Usage: AbcFs <mountpoint> <sourceDir>")
        System.exit(1)
    }

}
