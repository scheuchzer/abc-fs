package org.scheuchzer.abcfs
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Test
import java.net.URI
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Paths

class AbcFileSystemProviderTest {

    @Test
    fun testDirectory() {
        var root = Paths.get("src/test/resources/data").toAbsolutePath()
        println("root=${root}")
        FileSystems.newFileSystem(URI("abc", root.toString(), null, null), mutableMapOf<String, Object>())
        val path = Paths.get(URI.create("abc://${root}/a/aa"))
        assertThat(Files.isDirectory(path), `is`(true))

    }

    @Test
    fun testAbcDirectory() {
        val root = Paths.get("src/test/resources/data")
        println("root=${root.toAbsolutePath()}")
        FileSystems.newFileSystem(URI("abc", root.toAbsolutePath().toString(), null, null), mutableMapOf<String, Object>())
        val path = Paths.get(URI.create("abc://${root}/a"))
        assertThat(Files.isDirectory(path), `is`(true))

    }

    @Test
    fun testListLetterDirectory() {
        val root = Paths.get("src/test/resources/data")
        println("root=${root.toAbsolutePath()}")
        FileSystems.newFileSystem(URI("abc", root.toAbsolutePath().toString(), null, null), mutableMapOf<String, Object>())
        val path = Paths.get(URI.create("abc://${root}"))
        assertThat(Files.list(path).count(), `is`(25L))

    }

    @Test
    fun testListDirectory() {
        val root = Paths.get("src/test/resources/data")
        println("root=${root.toAbsolutePath()}")
        FileSystems.newFileSystem(URI("abc", root.toAbsolutePath().toString(), null, null), mutableMapOf<String, Object>())
        val path = Paths.get(URI.create("abc://${root}/a/aa"))
        assertThat(Files.list(path).count(), `is`(1L))

    }

    @Test
    fun testReadFile() {
        val root = Paths.get("src/test/resources/data")
        println("root=${root.toAbsolutePath()}")
        FileSystems.newFileSystem(URI("abc", root.toAbsolutePath().toString(), null, null), mutableMapOf<String, Object>())
        val path = Paths.get(URI.create("abc://${root}/a/aa/a-foo.txt/"))
        val content = String(Files.readAllBytes(path))
        assertThat(content, `is`("afoo"))

    }

    @Test
    fun testListLetterADirectory() {
        val root = Paths.get("src/test/resources/data")
        println("root=${root.toAbsolutePath()}")
        FileSystems.newFileSystem(URI("abc", root.toAbsolutePath().toString(), null, null), mutableMapOf<String, Object>())
        val path = Paths.get(URI.create("abc://${root}/a"))
        assertThat(Files.list(path).count(), `is`(3L))

    }

    @Test
    fun testListLetterZDirectory() {
        val root = Paths.get("src/test/resources/data")
        println("root=${root.toAbsolutePath()}")
        FileSystems.newFileSystem(URI("abc", root.toAbsolutePath().toString(), null, null), mutableMapOf<String, Object>())
        val path = Paths.get(URI.create("abc://${root}/z"))
        assertThat(Files.list(path).count(), `is`(0L))

    }
}