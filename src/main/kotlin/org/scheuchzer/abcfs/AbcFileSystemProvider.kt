package org.scheuchzer.abcfs

import java.io.IOException
import java.net.URI
import java.nio.ByteBuffer
import java.nio.channels.SeekableByteChannel
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.attribute.FileAttribute
import java.nio.file.attribute.FileAttributeView
import java.nio.file.spi.FileSystemProvider
import java.util.stream.Stream


/**
 * Created by celli on 01.01.17.
 */
class AbcFileSystemProvider : FileSystemProvider() {
    val SCHEME = "abc"
    val fileSystems = mutableMapOf<String, AbcFileSystem>()

    override fun getScheme(): String? {
        return SCHEME
    }

    @Throws(IOException::class)
    override fun newFileSystem(uri: URI, env: Map<String, *>): FileSystem? {
        synchronized(fileSystems) {
            var schemeSpecificPart = uri.schemeSpecificPart
            schemeSpecificPart = schemeSpecificPart.substring(2)
            var fileSystem = fileSystems[schemeSpecificPart]
            if (fileSystem != null) {
                //throw FileSystemAlreadyExistsException(schemeSpecificPart)
            }

            fileSystem = AbcFileSystem(this, schemeSpecificPart, env)
            fileSystems.put(schemeSpecificPart, fileSystem)
            return fileSystem
        }
    }

    override fun getFileSystem(uri: URI): AbcFileSystem? {
        synchronized(fileSystems) {
            var schemeSpecificPart = uri.schemeSpecificPart
            schemeSpecificPart = schemeSpecificPart.substring(2)
            //var fileSystem: AbcFileSystem? = fileSystems[0] ?: throw FileSystemNotFoundException(schemeSpecificPart)
            //return fileSystem
            return fileSystems.values.first()
        }
    }

    override fun getPath(uri: URI): Path? {
        val abcDirRx = """abc://.*/([a-z])""".toRegex()
        val letter = abcDirRx.matchEntire(uri.toString())?.groups?.get(1)?.value
        if (letter != null) {
            return LetterPath(getFileSystem(uri)!!, letter)
        }
        val rx = """abc://.*/[a-z]/(.*)""".toRegex()
        val value = rx.matchEntire(uri.toString())?.groups?.get(1)?.value
        if (value == null) {
            return LettersPath(getFileSystem(uri)!!)
        }
        val fileSystem = getFileSystem(uri)
        return AbcPath(fileSystem!!, value!!)
    }

    @Throws(IOException::class)
    override fun newByteChannel(path: Path, options: Set<OpenOption>, attrs: Array<FileAttribute<*>>): SeekableByteChannel? {
        if (path is AbcPath) {
            val data = Files.readAllBytes(path.getTargetPath())
            return object : SeekableByteChannel {
                internal var position: Long = 0


                @Throws(IOException::class)
                override fun read(dst: ByteBuffer): Int {
                    val l = Math.min(dst.remaining().toLong(), size() - position)
                    dst.put(data, position.toInt(), l.toInt())
                    position += l.toLong()
                    return l.toInt()
                }

                @Throws(IOException::class)
                override fun write(src: ByteBuffer): Int {
                    throw UnsupportedOperationException()
                }

                @Throws(IOException::class)
                override fun position(): Long {
                    return position
                }

                @Throws(IOException::class)
                override fun position(newPosition: Long): SeekableByteChannel {
                    position = newPosition
                    return this
                }

                @Throws(IOException::class)
                override fun size(): Long {
                    return data.size.toLong()
                }

                @Throws(IOException::class)
                override fun truncate(size: Long): SeekableByteChannel {
                    throw UnsupportedOperationException()
                }

                override fun isOpen(): Boolean {
                    return true
                }

                @Throws(IOException::class)
                override fun close() {
                }
            }
        }
        throw IOException("Only supports AbcPaths")
    }

    @Throws(IOException::class)
    override fun newDirectoryStream(dir: Path, filter: DirectoryStream.Filter<in Path>): DirectoryStream<Path>? {
        if (dir is AbcPath) {
            return object : DirectoryStream<Path> {
                private var stream: Stream<Path> = Files.list(dir.getTargetPath())
                override fun close() {
                    stream.close()
                }


                override fun iterator(): MutableIterator<Path> {
                    return stream.iterator()
                }

            }
        } else if (dir is LetterPath) {
            return object : DirectoryStream<Path> {
                private var stream: Stream<Path> = Files.list(Paths.get(dir.fileSystem.schemeSpecificPart))
                override fun close() {
                    stream.close()
                }


                override fun iterator(): MutableIterator<Path> {

                    return stream.filter {
                        it.fileName.toString().toLowerCase().startsWith(dir.letter)
                    }.iterator()
                }

            }
        } else if (dir is LettersPath) {
            return object : DirectoryStream<Path> {
                override fun iterator(): MutableIterator<Path> {
                    var result = mutableListOf<Path>()
                    'a'.until('z').map {
                        LetterPath(dir.fileSystem, it.toString())
                    }.toCollection(result)
                    return result.iterator()
                }

                override fun close() {
                }

            }
        } else {
            throw ProviderMismatchException()
        }

    }

    @Throws(IOException::class)
    override fun createDirectory(dir: Path, attrs: Array<FileAttribute<*>>) {
        throw UnsupportedOperationException("The abc file system is read only.")
    }

    @Throws(IOException::class)
    override fun delete(path: Path) {
        throw UnsupportedOperationException("The abc file system is read only.")
    }

    @Throws(IOException::class)
    override fun copy(source: Path, target: Path, vararg options: CopyOption) {

    }

    @Throws(IOException::class)
    override fun move(source: Path, target: Path, vararg options: CopyOption) {
        throw UnsupportedOperationException("The abc file system is read only.")
    }

    @Throws(IOException::class)
    override fun isSameFile(path: Path, path2: Path): Boolean {
        return false
    }

    @Throws(IOException::class)
    override fun isHidden(path: Path): Boolean {
        return false
    }

    @Throws(IOException::class)
    override fun getFileStore(path: Path): FileStore? {
        return null
    }

    @Throws(IOException::class)
    override fun checkAccess(path: Path, vararg modes: AccessMode) {

    }

    override fun <V : FileAttributeView> getFileAttributeView(path: Path, type: Class<V>, vararg options: LinkOption): V? {
        return null
    }

    @Throws(IOException::class)
    override fun <A : BasicFileAttributes> readAttributes(path: Path, type: Class<A>, vararg options: LinkOption): A? {
        if (path is AbcPath) {
            return Files.readAttributes(path.getTargetPath(), type, *options)
        } else if (path is LetterPath) {
            return Files.readAttributes(Paths.get("."), type, *options)
        } else if (path is LettersPath) {
            return Files.readAttributes(Paths.get("."), type, *options)
        }
        throw UnsupportedOperationException("Only AbcPaths supported")
    }

    @Throws(IOException::class)
    override fun readAttributes(path: Path, attributes: String, vararg options: LinkOption): Map<String, Any>? {
        return null
    }

    @Throws(IOException::class)
    override fun setAttribute(path: Path, attribute: String, value: Any, vararg options: LinkOption) {
        throw UnsupportedOperationException("The abc file system is read only.")
    }
}


