package org.scheuchzer.abcfs

import java.nio.file.*
import java.nio.file.attribute.UserPrincipalLookupService
import java.nio.file.spi.FileSystemProvider
import sun.text.normalizer.UTF16.append



class AbcFileSystem(val abcFileSystemProvider: AbcFileSystemProvider, val schemeSpecificPart: String, val env: Map<String, *>) : FileSystem() {
    override fun newWatchService(): WatchService {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun supportedFileAttributeViews(): MutableSet<String> {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isReadOnly(): Boolean {
        return true;
    }

    override fun getFileStores(): MutableIterable<FileStore> {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPath(first: String?, vararg more: String?): Path {
        var path: String
        if (more.isEmpty()) {
            path = first!!
        } else {
            val sb = StringBuilder()
            sb.append(first)
            more.forEach { s ->
                if (s!!.length > 0) {
                    if (sb.length > 0) {
                        sb.append('/')
                    }
                    sb.append(s)

                }
            }

            path = sb.toString()
        }
        if (path.length == 2) {
            return LetterPath(this, path.substring(1))
        } else if (path.length > 2) {
            return AbcPath(this, path.substring(3))
        }
        return LettersPath(this)
    }

    override fun provider(): FileSystemProvider {
        return abcFileSystemProvider
    }

    override fun isOpen(): Boolean {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getUserPrincipalLookupService(): UserPrincipalLookupService {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun close() {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPathMatcher(syntaxAndPattern: String?): PathMatcher {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getRootDirectories(): MutableIterable<Path> {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getSeparator(): String {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}