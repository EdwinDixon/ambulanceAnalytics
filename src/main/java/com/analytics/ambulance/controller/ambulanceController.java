package com.analytics.ambulance.controller;

import com.analytics.ambulance.entities.Ambulance;
import com.analytics.ambulance.repos.AmbulanceRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by lenovo on 16/7/17.
 */

@Controller
@RequestMapping("/")
public class ambulanceController {
    private Logger logger = LoggerFactory.getLogger(ambulanceController.class);

    private AmbulanceRepo ambulanceRepo;

    @Autowired
    public ambulanceController(AmbulanceRepo repo){
        this.ambulanceRepo = repo;
    }

    @RequestMapping("/ambulanceDetails")
    @ResponseBody
    public List<Ambulance> getAmbulanceDetails(){
        List<Ambulance> ambulanceList=ambulanceRepo.findAll();
        return ambulanceList;
    }




}
