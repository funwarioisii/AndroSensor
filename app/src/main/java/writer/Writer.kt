package writer


import android.os.Environment.getExternalStorageDirectory
import java.io.File
import java.io.FileOutputStream


class Writer (var fileName: String) {
    var mode = Mode.TEXT

    /**
     * インスタンス生成時に，指定されたファイル名が，すでに作られたファイルであれば，元のファイルを削除する
     */
    init {
        if (File("${getExternalStorageDirectory().path}/${this.fileName}").exists()) {
            File("${getExternalStorageDirectory().path}/${this.fileName}").delete()
        }
    }
    companion object {
        enum class Mode{
            CSV, TEXT, JSON
        }
    }

    fun write(message: String) : Boolean {
        return when(mode) {
            Mode.CSV -> {
                writeCSV(message)
            }
            Mode.TEXT -> {
                writeText(message)
            }
            Mode.JSON -> {
                writeJson(message)
            }
        }
    }

    fun write(messages: ArrayList<String>): Boolean {
        return when(mode) {
            Mode.CSV -> {
                writeCSV(messages)
            }
            Mode.TEXT -> {
                writeText(messages)
            }
            Mode.JSON -> {
                writeJson(messages)
            }
        }
    }

    fun modeSelect(newMode: Mode): Writer {
        mode = newMode
        fileName = fileName.split(".")
                .let { it.take(it.size-1) }
                .reduce { s1, s2 -> "$s1.$s2" }
                .toString()
                .let {
                    when(mode){
                        Mode.TEXT -> "$it.txt"
                        Mode.JSON -> "$it.json"
                        Mode.CSV -> "$it.csv"
                    }
                }
        return this
    }

    fun addInitialColumn(column: Array<String>) {
        val initialColumn = column.reduce{s1, s2 -> "$s1,$s2"}.toString()
        writeCSV(initialColumn)
    }

    fun changeFilename(fileName: String) {
        this.fileName = fileName
    }

    private fun writeCSV(message: String): Boolean {
        if (!this.fileName.endsWith(".csv")){
            return false
        }
        val fos : FileOutputStream?
        fos = FileOutputStream("${getExternalStorageDirectory().path}/${this.fileName}", true)
        fos.write(message.toByteArray())
        fos.close()
        return true
    }

    private fun writeCSV(elements: ArrayList<String>): Boolean {
        if (!this.fileName.endsWith(".csv")){
            return false
        }
        val message = "\n"+elements.reduce{ s1, s2 -> "$s1,$s2"}.toString()
        val fos : FileOutputStream?
        fos = FileOutputStream("${getExternalStorageDirectory().path}/${this.fileName}", true)
        fos.write(message.toByteArray())
        fos.close()
        return true
    }

    private fun writeJson(message: String): Boolean = "hoge" in message
    private fun writeJson(messages: ArrayList<String>): Boolean = "hoge" in messages


    private fun writeText(message: String): Boolean {
        val fos : FileOutputStream?
        fos = FileOutputStream("${getExternalStorageDirectory().path}/${this.fileName}", true)
        fos.write(message.toByteArray())
        fos.close()
        return true
    }

    private fun writeText(messages: ArrayList<String>): Boolean {
        val fos: FileOutputStream?
        fos = FileOutputStream("${getExternalStorageDirectory().path}/${this.fileName}", true)
        messages.forEachIndexed { index, message ->
            when (index) {
                0 -> fos.write(message.toByteArray())
                else -> fos.write("\n$message".toByteArray())
            }
        }
        fos.close()
        return true

    }
}