package io.funwarioisii.androsensor

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import writer.Writer

class MainActivity : AppCompatActivity() , SensorEventListener{
    private val gyroWriter = Writer("gyro_sample.csv")
            .modeSelect(Writer.Companion.Mode.CSV)
    private val axelWriter = Writer("axel_sample.csv")
            .modeSelect(Writer.Companion.Mode.CSV)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val manager = this.getSystemService(android.content.Context.SENSOR_SERVICE) as SensorManager

        // add target sensor type
        val axelSensor = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val gyroSensor = manager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        manager.registerListener(this, axelSensor, SensorManager.SENSOR_DELAY_FASTEST)
        manager.registerListener(this, gyroSensor, SensorManager.SENSOR_DELAY_FASTEST)

        gyroWriter.changeFilename("gyro_sample_${System.currentTimeMillis()}.csv")
        axelWriter.changeFilename("axel_sample_${System.currentTimeMillis()}.csv")

        gyroWriter.addInitialColumn(arrayOf("nanosecond","x","y","z"))
        axelWriter.addInitialColumn(arrayOf("nanosecond","x","y","z"))

    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {

    }

    override fun onSensorChanged(event: SensorEvent) {
        contentWrite(event)
        // and more...
    }

    private fun contentWrite(event: SensorEvent) {
        val writer = when (event.sensor.type) {
            Sensor.TYPE_GYROSCOPE -> gyroWriter
            Sensor.TYPE_ACCELEROMETER -> axelWriter
            else -> Writer("error")
        }
        val result = event.values.clone().map { fl: Float -> fl.toString() }
        val timestamp = event.timestamp

        val writeContent = arrayListOf<String>(timestamp.toString())
        result.forEach { ele -> writeContent.add(ele) }

        writer.write(writeContent)
    }
}
