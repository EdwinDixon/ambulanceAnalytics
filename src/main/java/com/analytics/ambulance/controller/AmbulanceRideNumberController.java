package com.analytics.ambulance.controller;

import com.analytics.ambulance.entities.Ambulance;
import com.analytics.ambulance.entities.Incident;
import com.analytics.ambulance.repos.AmbulanceRepo;
import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

/**
 * Created by lenovo on 16/9/17.
 */
@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/")
public class AmbulanceRideNumberController {

    private AmbulanceRepo ambulanceRepo;

    @Autowired
    public AmbulanceRideNumberController(AmbulanceRepo repo){
        this.ambulanceRepo = repo;
    }

    @RequestMapping(value = "/ride_number/{ambulanceNo}")
    @ResponseBody
    public long getAmbulanceRideNumber(@PathVariable("ambulanceNo")
                                                   String ambulanceNo){
        AggregationResults<Incident> aggregate = null;

        MongoTemplate mongoTemplate = new MongoTemplate(new SimpleMongoDbFactory(new MongoClient(), "ambulancedb"));

        //projecting only what is needed to the next aggregation pipeline
        AggregationOperation yearProjection = Aggregation.project()
                .andExpression("year(date)").as("year")
                .andExpression("routeTaken").as("routeTaken")
                .andExpression("date").as("date");

        AggregationOperation ambulanceIdMatch = Aggregation.match(new Criteria("Ambulance_no").is(ambulanceNo));

        AggregationOperation dailyAggregation = Aggregation.match(new Criteria("date")
                .gt(Date.from(LocalDate.now()
                        .minusDays(1)
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant()))
                .lt(Date.from(LocalDate.now().plusDays(1)
                        .atStartOfDay(ZoneId.systemDefault()).toInstant())));

        Aggregation aggregation = newAggregation(
                ambulanceIdMatch,
                dailyAggregation,
                yearProjection
        );


        aggregate = mongoTemplate.aggregate(aggregation,
                "incidents", Incident.class);

        Ambulance ambulance = ambulanceRepo.findByLicensePlateNumber(ambulanceNo);
        System.out.println(ambulance);
        long result;

        if(ambulance.getStatus().equals("onRide")){
            System.out.println(ambulance.getStatus());
            result = aggregate.getMappedResults().size()+1;
            return result;
        }

        return aggregate.getMappedResults().size();

    }
}
