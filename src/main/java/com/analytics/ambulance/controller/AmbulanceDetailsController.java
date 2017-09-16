package com.analytics.ambulance.controller;

import com.analytics.ambulance.LocationRequest;
import com.analytics.ambulance.entities.Ambulance;
import com.analytics.ambulance.repos.AmbulanceDetailsRepo;
import com.analytics.ambulance.repos.AmbulanceRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by lenovo on 19/9/17.
 */
@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/")
public class AmbulanceDetailsController {
    private AmbulanceDetailsRepo ambulanceDetailsRepo;

    @Autowired
    public AmbulanceDetailsController(AmbulanceDetailsRepo ambulanceDetailsRepo){
        this.ambulanceDetailsRepo = ambulanceDetailsRepo;
    }

    @RequestMapping(value = "/ambulanceDetails")
    @ResponseBody
    public List<Ambulance> getAmbulanceDetails(){
        List<Ambulance> ambulanceDetails = ambulanceDetailsRepo.findAll();
        return ambulanceDetails;

    }

    @RequestMapping(value = "/ambulanceDetailsCount")
    @ResponseBody
    public long getAmbulanceDetailsCount(){
                long count = ambulanceDetailsRepo.count();

                return count;
    }
}
