package com.analytics.ambulance.controller;

import com.analytics.ambulance.LocationRequest;
import com.analytics.ambulance.entities.Ambulance;
import com.analytics.ambulance.repos.AmbulanceRepo;
import com.sun.org.apache.xpath.internal.SourceTree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;

import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.jmx.support.MetricType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by lenovo on 29/7/17.
 */
@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/")
public class NearbyAmbulanceController {

    private AmbulanceRepo ambulanceRepo;

    @Autowired
    public NearbyAmbulanceController(AmbulanceRepo repo){
        this.ambulanceRepo = repo;
    }

    @RequestMapping(value="/nearbyAmbulance",method = RequestMethod.POST)
    @ResponseBody
    public List<Ambulance> getAmbulanceDetails(
            @RequestBody LocationRequest locationRequest){
        System.out.println("lati "+locationRequest.getLatitude()+ " long "+locationRequest.getLongitude()+" d "+locationRequest.getDistance());
//        List<Ambulance> ambulanceList=this.ambulanceRepo.findByLocationNear(new Point(Double.valueOf(locationRequest.getLongitude()),Double.valueOf(locationRequest.getLatitude())),new Distance(locationRequest.getDistance(), Metrics.KILOMETERS));
        Point point = new Point(locationRequest.getLongitude(), locationRequest.getLatitude());
        Distance distance = new Distance(locationRequest.getDistance(),Metrics.KILOMETERS);
        System.out.println("normalized "+distance.getNormalizedValue());
        System.out.println("value "+distance.getValue());
        System.out.println("metric "+distance.getMetric());
//        System.out.println(distance.getMetric());
        List<Ambulance> ambulanceList=this.ambulanceRepo.findByLocationNear(point,distance);
        System.out.println(ambulanceList);
        return ambulanceList;
    }
}
