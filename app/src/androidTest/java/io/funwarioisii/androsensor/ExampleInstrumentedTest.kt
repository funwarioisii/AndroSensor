package io.funwarioisii.androsensor

import android.os.Environment
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import writer.Writer
import java.io.File
import java.io.FileInputStream

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()
        assertEquals("io.funwarioisii.androsensor", appContext.packageName)
    }
}

@RunWith(AndroidJUnit4::class)
class WriterAndroidTest {
    // やらないこと：
    //      書き込み権限がなくて落ちるときの対策（標準のままがよかろう）

    @Test
    fun Writerオブジェクトがこけずに生成されるか() {
        val writer = Writer("test.txt")
        assertEquals(true, writer is Writer)
        arrayOf("test.txt", "test.csv", "test.json").map { fileName -> 後始末(fileName) }
    }

    @Test
    fun writeメソッドはエラーを起こさないか() {
        val writer = Writer("test.txt")
        assertEquals(true, writer.write("Hello"))
        arrayOf("test.txt", "test.csv", "test.json").map { fileName -> 後始末(fileName) }
    }

    @Test
    fun writeメソッドは正しく書き込めているか(){
        val writer = Writer("test.txt")
        writer.write("Hello")

        val file = File("${Environment.getExternalStorageDirectory().path}/test.txt")
        val fis = FileInputStream(file)
        val readByte = ByteArray(fis.available())
        fis.read(readByte)
        val readText = String(readByte)
        assertEquals("Hello", readText)
        arrayOf("test.txt", "test.csv", "test.json").map { fileName -> 後始末(fileName) }

    }

    @Test
    fun writeCSVメソッドが正しく動いているか() {
        val result = Writer("test.txt").let {
            it.javaClass.getDeclaredMethod("writeText", String::class.java)
                    .apply { isAccessible = true }
                    .invoke(it, "Hello")
        }
        assertEquals(true, result)
        arrayOf("test.txt", "test.csv", "test.json").map { fileName -> 後始末(fileName) }
    }

    @Test
    fun モードを切り替えられるか() {
        var writer = Writer("test.txt")
        writer = writer.modeSelect(Writer.Companion.Mode.JSON)
        assertEquals(Writer.Companion.Mode.JSON, writer.mode)
        assertEquals("test.json", writer.fileName)
        arrayOf("test.txt", "test.csv", "test.json").map { fileName -> 後始末(fileName) }
    }

    @Test
    fun モード切り替えでファイル名は正しく変更されるか() {
        var writer = Writer("test.2.json")
        writer = writer.modeSelect(Writer.Companion.Mode.JSON)
        assertEquals(Writer.Companion.Mode.JSON, writer.mode)
        assertEquals("test.2.json", writer.fileName)
    }

    @Test
    fun CSVモードで最初の行を書けるか() {
        val writer = Writer("test.txt").modeSelect(Writer.Companion.Mode.CSV)
        writer.addInitialColumn(arrayOf("ID", "Group", "Name"))
        arrayOf("test.txt", "test.csv", "test.json").map { fileName -> 後始末(fileName) }
    }

    @Test
    fun CSVの書き込みがうまくいくか(){
        val writer = Writer("test.txt").modeSelect(Writer.Companion.Mode.CSV)
        writer.addInitialColumn(arrayOf("ID", "Group", "Name"))
        writer.write(arrayListOf("1", "H", "funwari"))
        writer.write(arrayListOf("2", "D", "oisii"))

        val file = File("${Environment.getExternalStorageDirectory().path}/test.csv")
        val fis = FileInputStream(file)
        val readByte = ByteArray(fis.available())
        fis.read(readByte)
        val readText = String(readByte)
        assertEquals("ID,Group,Name\n1,H,funwari\n2,D,oisii", readText)
        arrayOf("test.txt", "test.csv", "test.json").map { fileName -> 後始末(fileName) }
    }

    @Test
    fun 同じファイル名で新しくインスタンスを生成するときは元のファイルを消す() {
        val writer = Writer("test.txt")
        writer.write("yo")
        val writer2 = Writer("test.txt")


        assertEquals(false, File("${Environment.getExternalStorageDirectory().path}/test.txt").exists())
        arrayOf("test.txt", "test.csv", "test.json").map { fileName -> 後始末(fileName) }
    }

    @Test
    fun ファイル名を変更する() {
        val writer = Writer("test.txt")
        writer.changeFilename("test2.txt")
        writer.write("")
        assertEquals(true, File("${Environment.getExternalStorageDirectory().path}/test2.txt").exists())
        arrayOf("test.txt", "test.csv", "test.json", "test2.txt").map { fileName -> 後始末(fileName) }
    }

    fun 後始末(fileName: String) {
        val file = File("${Environment.getExternalStorageDirectory().path}/${fileName}")
        if (file.exists()){
            file.delete()
        }
    }
}
