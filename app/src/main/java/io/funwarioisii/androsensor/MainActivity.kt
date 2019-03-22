package io.funwarioisii.androsensor

import android.app.PendingIntent
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.nfc.NfcAdapter
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import writer.FirebaseDBWrite
import writer.Writer
import java.util.*


class MainActivity : AppCompatActivity() , SensorEventListener{
    private val gyroWriter = Writer("gyro_sample.csv")
            .modeSelect(Writer.Companion.Mode.CSV)
    private val axelWriter = Writer("axel_sample.csv")
            .modeSelect(Writer.Companion.Mode.CSV)
    private var nfcAdapter: NfcAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val manager = this.getSystemService(android.content.Context.SENSOR_SERVICE) as SensorManager
        nfcAdapter = NfcAdapter.getDefaultAdapter(applicationContext)

        val availableSensorList = manager.getSensorList(Sensor.TYPE_ALL)
        val targetSensorList = listOf(
                Sensor.TYPE_ACCELEROMETER,
                Sensor.TYPE_GYROSCOPE,
                Sensor.TYPE_AMBIENT_TEMPERATURE,
                Sensor.TYPE_LIGHT)


        targetSensorList.forEach {
            if (it in availableSensorList.map { availableSensor -> availableSensor.type }) {
                val sensor = manager.getDefaultSensor(it)
                when (sensor.type) {
                    Sensor.TYPE_LIGHT -> manager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
                    else -> manager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST)
                }
            }
        }

        gyroWriter.changeFilename("gyro_sample_${System.currentTimeMillis()}.csv")
        axelWriter.changeFilename("axel_sample_${System.currentTimeMillis()}.csv")

        gyroWriter.addInitialColumn(arrayOf("nanosecond","x","y","z"))
        axelWriter.addInitialColumn(arrayOf("nanosecond","x","y","z"))

    }

    override fun onResume() {
        super.onResume()

        val intent = Intent(this, this.javaClass)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, 0)
        nfcAdapter?.enableForegroundDispatch(this, pendingIntent, null, null)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        val uid = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID)
        val arrangedUid = Arrays.toString(uid)

        // 学生証 [1, 20, -60, 14, -37, 22, -29, 30]
        Toast.makeText(applicationContext, arrangedUid, Toast.LENGTH_SHORT).show()
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {

    }

    override fun onSensorChanged(event: SensorEvent) = when (event.sensor.type) {
            Sensor.TYPE_LIGHT -> writeToFirebase(event)
            else -> contentWrite(event)
    }

    private fun writeToFirebase(event: SensorEvent) =
            FirebaseDBWrite.updateChild(event.timestamp.toString(), event.values[0])

    private fun contentWrite(event: SensorEvent) {
        val writer = when (event.sensor.type) {
            Sensor.TYPE_GYROSCOPE -> gyroWriter
            Sensor.TYPE_ACCELEROMETER -> axelWriter
            else -> null
        }
        val result = event.values.clone().map { fl: Float -> fl.toString() }
        val timestamp = event.timestamp

        val writeContent = arrayListOf(timestamp.toString())
        result.forEach { ele -> writeContent.add(ele) }

        writer?.write(writeContent)
    }
}
