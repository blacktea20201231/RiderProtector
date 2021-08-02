import Constants.PERMISSION_BACKGROUND_LOCATION_REQUEST_CODE
import Constants.PERMISSION_LOCATION_REQUEST_CODE
import android.Manifest
import android.content.Context
import android.os.Build
import androidx.fragment.app.Fragment
import com.vmadalin.easypermissions.EasyPermissions

object Permissions {
    fun hasLocationPermission(context: Context) =
        EasyPermissions.hasPermissions(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

    fun requestLocationPermission(fragment: Fragment){
        EasyPermissions.requestPermissions(
            fragment,
            "This application cannot work without Location Permission",
            PERMISSION_LOCATION_REQUEST_CODE,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }


}