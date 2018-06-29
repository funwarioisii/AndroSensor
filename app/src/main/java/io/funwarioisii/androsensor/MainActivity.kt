package io.funwarioisii.androsensor

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import me.mattak.moment.Moment
import writer.Writer

class MainActivity : AppCompatActivity() , SensorEventListener{
    val writer = Writer("sample.csv")
            .modeSelect(Writer.Companion.Mode.CSV)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val manager = this.getSystemService(android.content.Context.SENSOR_SERVICE) as SensorManager
        val sensor = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        writer.addInitialColumn(arrayOf("time","axel"))
        manager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSensorChanged(event: SensorEvent) {
        val result = event.values.clone()
        val moment = Moment()
        val time = moment.year.toString() +
                moment.month.toString() +
                moment.day.toString() +
                moment.hour.toString() +
                moment
        val writeContent = arrayOf(time, result.toString())
        writer.write(writeContent)
    }
}
