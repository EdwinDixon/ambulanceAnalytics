package com.analytics.ambulance.controller;

import com.analytics.ambulance.entities.Incident;
import com.analytics.ambulance.repos.IncidentRepo;
import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

/**
 * Created by lenovo on 27/8/17.
 */
@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/")
public class HeatMapController {

    private IncidentRepo incidentRepo;

    @Autowired
    public HeatMapController(IncidentRepo repo) {
        this.incidentRepo = repo;
    }

    @RequestMapping(value = "/heatMap/{heatMapType}")
    @ResponseBody
    public List<Incident> getAmbulanceDetails(@PathVariable("heatMapType") String heatMapType) {

        AggregationResults<Incident> aggregate = null;

        MongoTemplate mongoTemplate = new MongoTemplate(new SimpleMongoDbFactory(new MongoClient(), "ambulancedb"));


        if (heatMapType.equals("daily")) {


            //groups the incidents occured on the current date
            AggregationOperation dailyAggregation = Aggregation.match(new Criteria("date")
                    .gt(Date.from(LocalDate.now()
                            .minusDays(1)
                            .atStartOfDay(ZoneId.systemDefault())
                            .toInstant())));


            //projecting only what is needed to the next aggregation pipeline
            AggregationOperation yearProjection = Aggregation.project()
                    .andExpression("year(date)").as("year")
                    .andExpression("incidentLatLng").as("incidentLatLng");


            //groups the incidents based on the incident location and year of incident and will also count them
            AggregationOperation groupOperation = Aggregation.group(fields().and("year").and("incidentLatLng"))
                    .count().as("count");


            //takes groups having more than 1 incidents into consideration
            AggregationOperation matchOperation = Aggregation.match(new Criteria("count").gt(1));


            //helps to unwind the _id object
            AggregationOperation unwindOperation = Aggregation.unwind("_id");


            //changes the object structure to make the result compatible with the Incident entity
            AggregationOperation projectOperation = Aggregation.project()
//                .andExpression("_id.date").as("date")
                    .andExpression("_id.year").as("date")
                    .andExpression("_id.incidentLatLng").as("incidentLatLng");


            Aggregation aggregation = newAggregation(
                    dailyAggregation,
                    yearProjection,
                    groupOperation,
                    matchOperation,
                    unwindOperation,
                    projectOperation
            );


            aggregate = mongoTemplate.aggregate(aggregation,
                    "incidents", Incident.class);

        }
        return aggregate.getMappedResults();

    }
}