package com.analytics.ambulance.controller;

import com.analytics.ambulance.entities.Incident;
import com.analytics.ambulance.repos.IncidentRepo;
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
import java.time.temporal.WeekFields;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.fields;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

/**
 * Created by lenovo on 29/9/17.
 */
@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/")
public class HighAmbulanceProximityLocations {

    private IncidentRepo incidentRepo;

    @Autowired
    public HighAmbulanceProximityLocations(IncidentRepo repo) {
        this.incidentRepo = repo;
    }

    @RequestMapping(value = "/ambulanceProximity/{heatMapType}")
    @ResponseBody
    public List<Incident> getHighProximityLocations(@PathVariable("heatMapType") String heatMapType) {

        AggregationResults<Incident> aggregate = null;

        MongoTemplate mongoTemplate = new MongoTemplate(new SimpleMongoDbFactory(new MongoClient(), "ambulancedb"));

        //projecting only what is needed to the next aggregation pipeline
        AggregationOperation routeProjection = Aggregation.project()
                .andExpression("routeTaken").as("routeTaken");
//                .andExpression("routeTaken").as("routeTaken");

        AggregationOperation groupOperation = Aggregation.group(fields()).push("routeTaken").as("routeTaken");


        AggregationOperation groupOperation3 = Aggregation.group(fields()).push("routeTaken").as("routeTaken");


        AggregationOperation unwind1 =Aggregation.unwind("routeTaken");
        AggregationOperation unwind2 = Aggregation.unwind("routeTaken");


        //groups the incidents based on the incident location and year of incident and will also count them
        AggregationOperation groupOperation1 = Aggregation.group(fields("routeTaken"))
                .count().as("date");


        //takes groups having more than 1 incidents into consideration
        AggregationOperation matchOperation = Aggregation.match(new Criteria("count").gt(2));

        AggregationOperation routeTakenProject = Aggregation.project().andExpression("routeTaken").as("routeTaken");


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
                    routeProjection,
                    groupOperation,
                    unwind1,
                    unwind2,
                    groupOperation3
            );


            aggregate = mongoTemplate.aggregate(aggregation,
                    "incidents", Incident.class);

            System.out.println(aggregate.getMappedResults().size());
            return aggregate.getMappedResults();

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
                    routeProjection,
                    groupOperation,
                    unwind1,
                    unwind2,
                    groupOperation3
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
                    routeProjection,
                    groupOperation,
                    unwind1,
                    unwind2,
                    groupOperation3
//                    groupOperation1

//                    routeTakenProject

//
//                    routeTakenProject
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
