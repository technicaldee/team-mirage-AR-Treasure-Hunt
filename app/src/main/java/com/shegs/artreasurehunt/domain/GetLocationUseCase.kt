package com.shegs.artreasurehunt.domain

import android.os.Build
import androidx.annotation.RequiresApi
import com.shegs.artreasurehunt.data.ILocationService
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLocationUseCase @Inject constructor(
    private val locationService: ILocationService
) {
    @RequiresApi(Build.VERSION_CODES.S)
    operator fun invoke(): Flow<LatLng?> = locationService.requestLocationUpdates()

}