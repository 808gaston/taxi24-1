package bk.project.controllers;



import bk.project.ResponseBean;
import bk.project.domain.Driver;
import bk.project.domain.Rider;
import bk.project.domain.Trips;
import bk.project.services.DriverService;
import bk.project.services.RiderService;
import bk.project.services.ServiceforAll;
import bk.project.services.TripsService;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/trip")
public class TripController {
    OkHttpClient client = new OkHttpClient();
    @Autowired
    private TripsService tripsService;
    @Autowired
    private DriverService driverService;
    @Autowired
    private RiderService riderService;
    private ServiceforAll s = new ServiceforAll();

    @PostMapping("/rider/{rider_id}/driver/{driver_id}/destination/{latitude}/{longitude}")
    public ResponseEntity<Object> createDriver(@RequestBody Trips trip, @PathVariable("rider_id") int rider_id,@PathVariable("driver_id") int driver_id,@PathVariable("latitude")double latitude, @PathVariable("longitude")double longitude){
        ResponseBean responseBean = new ResponseBean();
        Rider rider = riderService.findById(rider_id);
        Driver driver = driverService.findById(driver_id);
        try {
            if(rider != null && driver != null){
                trip.setFromLat(rider.getLatitude());
                trip.setFromLng(rider.getLongitude());
                trip.setDriverId(driver.getId());
                trip.setRiderId(rider.getId());
                trip.setToLat(latitude);
                trip.setToLng(longitude);
                trip.setDistance(Math.round(s.getDistanceFromLatLonInKm(rider.getLatitude(),rider.getLongitude(),latitude,longitude)));
                responseBean.setCode(200);
                responseBean.setDescription("Trip created");
                responseBean.setObject(tripsService.createTrip(trip));
            }
            else {
                responseBean.setCode(300);
                responseBean.setDescription("rider or Driver not found");
                responseBean.setObject(null);
            }

        }catch (Exception e){
            e.printStackTrace();
            responseBean.setCode(300);
            responseBean.setDescription(" There is an error");
            responseBean.setObject(null);
        }
        return new ResponseEntity<Object>(responseBean, HttpStatus.OK);
    }

    @GetMapping("/trips/{id}")
    public ResponseEntity<Object> TripbyId(@RequestParam("id") int id){
        Trips trips = tripsService.findOne(id);
        ResponseBean responseBean = new ResponseBean();
        try {
            if(trips != null){
                responseBean.setCode(200);
                responseBean.setDescription("Trip founded");
                responseBean.setObject(trips);
            }else{
                responseBean.setCode(300);
                responseBean.setDescription("Not Found");
                responseBean.setObject(null);
            }
        }catch (Exception e){
            e.printStackTrace();
            responseBean.setCode(300);
            responseBean.setDescription("Not Found");
            responseBean.setObject(null);
        }

        return new ResponseEntity<Object>(responseBean,HttpStatus.OK);
    }

    @GetMapping("/completed")
    public ResponseEntity<Object> completedTrip(){
        ResponseBean responseBean = new ResponseBean();
        responseBean.setCode(200);
        responseBean.setDescription("All Completed trips");
        responseBean.setObject(tripsService.completedTrips());
        return new ResponseEntity<Object>(responseBean,HttpStatus.OK);
    }

    @PutMapping(value = "/complete_trips/{id}/status/{status}")
    public ResponseEntity<Object> completeActiveTrip(@PathVariable("id") int id,@PathVariable("status") int status){
        Trips trips = tripsService.findOne(id);
        ResponseBean responseBean = new ResponseBean();
        if (trips != null){
            try {
                trips.setStatus(status);
                trips.setCompletedAt(new Date().toString());
                responseBean.setCode(200);
                responseBean.setDescription("trip well Completed");
                responseBean.setObject(tripsService.completeTrip(trips));

            }catch (Exception e){
                responseBean.setCode(300);
                responseBean.setDescription("Action have some issues");
                responseBean.setObject(null);
            }
        }else {
            responseBean.setCode(300);
            responseBean.setDescription("trips not found");
            responseBean.setObject(null);
        }
        return new ResponseEntity<Object>(responseBean, HttpStatus.OK);
    }
}
