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
import java.time.temporal.WeekFields;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

/**
 * Created by lenovo on 27/8/17.
 */
@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/")
public class AccidentProneAreasController {

    private IncidentRepo incidentRepo;

    @Autowired
    public AccidentProneAreasController(IncidentRepo repo) {
        this.incidentRepo = repo;
    }

    @RequestMapping(value = "/accidentProneAreas/{heatMapType}")
    @ResponseBody
    public List<Incident> getAmbulanceDetails(@PathVariable("heatMapType") String heatMapType) {

        AggregationResults<Incident> aggregate = null;

        MongoTemplate mongoTemplate = new MongoTemplate(new SimpleMongoDbFactory(new MongoClient(), "ambulancedb"));

        //projecting only what is needed to the next aggregation pipeline
        AggregationOperation yearProjection = Aggregation.project()
                .andExpression("year(date)").as("year")
                .andExpression("incidentLatLng").as("incidentLatLng")
                .andExpression("date").as("date");


        //groups the incidents based on the incident location and year of incident and will also count them
//        AggregationOperation groupOperation = Aggregation.group(fields().and("year").and("incidentLatLng"))
//                .count().as("count");


        //takes groups having more than 1 incidents into consideration
//        AggregationOperation matchOperation = Aggregation.match(new Criteria("count").gt(1));


        //helps to unwind the _id object
//        AggregationOperation unwindOperation = Aggregation.unwind("_id");


        //changes the object structure to make the result compatible with the Incident entity


        System.out.println("heat map type "+heatMapType);
        if (heatMapType.equals("daily")) {


            //groups the incidents occured on the current date
            AggregationOperation dailyAggregation = Aggregation.match(new Criteria("date")
                    .gt(Date.from(LocalDate.now()
                            .minusDays(1)
                            .atStartOfDay(ZoneId.systemDefault())
                            .toInstant()))
                    .lt(Date.from(LocalDate.now().plusDays(1)
                            .atStartOfDay(ZoneId.systemDefault()).toInstant())));


  /*          System.out.println("what I printed first "+LocalDate.now().with(WeekFields.of(Locale.US).dayOfWeek(), 1L));
            System.out.println("what came next "+LocalDate.now().with(WeekFields.of(Locale.US).getFirstDayOfWeek()));
            System.out.println("first one "+Date.from(LocalDate.now().with(WeekFields.of(Locale.US).dayOfWeek(), 1L)
                    .atStartOfDay(ZoneId.systemDefault()).toInstant()));
            System.out.println("second one "+Date.from(LocalDate.now().with(WeekFields.of(Locale.US).getFirstDayOfWeek())
            .atStartOfDay(ZoneId.systemDefault()).toInstant()));*/



            Aggregation aggregation = newAggregation(
                    dailyAggregation,
                    yearProjection
            );


            aggregate = mongoTemplate.aggregate(aggregation,
                    "incidents", Incident.class);

        }
        else if(heatMapType.equals("weekly"))
        {
            System.out.println(heatMapType);



            //groups the incidents occured in the current week
            AggregationOperation weekly = Aggregation.match(new Criteria("date")
                    .gte(Date.from(LocalDate.now().with(WeekFields.of(Locale.US).dayOfWeek(), 1L)
                            .atStartOfDay(ZoneId.systemDefault()).toInstant()))
                    .lt(Date.from(LocalDate.now().plusDays(1)
                            .atStartOfDay(ZoneId.systemDefault()).toInstant())));

            System.out.println(weekly.toString());


            Aggregation aggregation = newAggregation(
                    weekly,
                    yearProjection
            );


            aggregate = mongoTemplate.aggregate(aggregation,
                    "incidents", Incident.class);
        }
        else if(heatMapType.equals("monthly"))
        {
            AggregationOperation monthly = Aggregation.match(new Criteria("date")
                    .gte(Date.from(LocalDate.now().withDayOfMonth(1)
                            .atStartOfDay(ZoneId.systemDefault()).toInstant()))
                    .lt(Date.from(LocalDate.now().plusDays(1)
                            .atStartOfDay(ZoneId.systemDefault()).toInstant())));




            Aggregation aggregation = newAggregation(
                    monthly,
                    yearProjection

            );


            aggregate = mongoTemplate.aggregate(aggregation,
                    "incidents", Incident.class);
        }
        else{
            return null;
        }
        return aggregate.getMappedResults();

    }
}