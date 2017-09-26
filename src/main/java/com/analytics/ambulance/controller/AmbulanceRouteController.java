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
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.WeekFields;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.fields;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

/**
 * Created by lenovo on 19/9/17.
 */
@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/")
public class AmbulanceRouteController {

    private IncidentRepo incidentRepo;

    @Autowired
    public AmbulanceRouteController(IncidentRepo repo) {
        this.incidentRepo = repo;
    }

    @RequestMapping(value = "/routesTaken/{ambulanceNo}/{routesTakenType}")
    @ResponseBody
    public List<Incident> getAmbulanceDetails(@PathVariable("routesTakenType") String routesTakenType,@PathVariable("ambulanceNo")
                                              String ambulanceNo) {
        AggregationResults<Incident> aggregate = null;

        MongoTemplate mongoTemplate = new MongoTemplate(new SimpleMongoDbFactory(new MongoClient(), "ambulancedb"));

        //projecting only what is needed to the next aggregation pipeline
        AggregationOperation yearProjection = Aggregation.project()
                .andExpression("year(date)").as("year")
                .andExpression("routeTaken").as("routeTaken")
                .andExpression("date").as("date");

        AggregationOperation ambulanceIdMatch = Aggregation.match(new Criteria("Ambulance_no").is(ambulanceNo));
        AggregationOperation ridesCompleted = Aggregation.match(new Criteria("currentRoute").is(false));



        System.out.println("heat map type "+routesTakenType);
        if (routesTakenType.equals("daily")) {


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
                    ambulanceIdMatch,
                    ridesCompleted,
                    dailyAggregation,
                    yearProjection
            );


            aggregate = mongoTemplate.aggregate(aggregation,
                    "incidents", Incident.class);

        }
        else if(routesTakenType.equals("weekly"))
        {
            System.out.println(routesTakenType);



            //groups the incidents occured in the current week
            AggregationOperation weekly = Aggregation.match(new Criteria("date")
                    .gte(Date.from(LocalDate.now().with(WeekFields.of(Locale.US).dayOfWeek(), 1L)
                            .atStartOfDay(ZoneId.systemDefault()).toInstant()))
                    .lt(Date.from(LocalDate.now()
                            .atStartOfDay(ZoneId.systemDefault()).toInstant())));

            System.out.println(weekly.toString());


            Aggregation aggregation = newAggregation(
                    ambulanceIdMatch,
                    ridesCompleted,
                    weekly,
                    yearProjection
            );


            aggregate = mongoTemplate.aggregate(aggregation,
                    "incidents", Incident.class);
        }
        else if(routesTakenType.equals("monthly"))
        {
            AggregationOperation monthly = Aggregation.match(new Criteria("date")
                    .gte(Date.from(LocalDate.now().withDayOfMonth(1)
                            .atStartOfDay(ZoneId.systemDefault()).toInstant()))
                    .lt(Date.from(LocalDate.now()
                            .atStartOfDay(ZoneId.systemDefault()).toInstant())));




            Aggregation aggregation = newAggregation(
                    ambulanceIdMatch,
                    ridesCompleted,
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



    @RequestMapping(value = "/ambulanceRoutes/{routesTakenType}")
    @ResponseBody
    public List<Incident> getUsualAbmulanceRoutes(@PathVariable("routesTakenType") String routesTakenType) {
        AggregationResults<Incident> aggregate = null;

        MongoTemplate mongoTemplate = new MongoTemplate(new SimpleMongoDbFactory(new MongoClient(), "ambulancedb"));

        //projecting only what is needed to the next aggregation pipeline
        AggregationOperation yearProjection = Aggregation.project()
                .andExpression("year(date)").as("year")
                .andExpression("routeTaken").as("routeTaken")
                .andExpression("date").as("date");

        AggregationOperation ridesCompleted = Aggregation.match(new Criteria("currentRoute").is(false));



        System.out.println("heat map type "+routesTakenType);
        if (routesTakenType.equals("daily")) {


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
                    ridesCompleted,
                    dailyAggregation,
                    yearProjection
            );


            aggregate = mongoTemplate.aggregate(aggregation,
                    "incidents", Incident.class);

        }
        else if(routesTakenType.equals("weekly"))
        {
            System.out.println(routesTakenType);



            //groups the incidents occured in the current week
            AggregationOperation weekly = Aggregation.match(new Criteria("date")
                    .gte(Date.from(LocalDate.now().with(WeekFields.of(Locale.US).dayOfWeek(), 1L)
                            .atStartOfDay(ZoneId.systemDefault()).toInstant()))
                    .lt(Date.from(LocalDate.now()
                            .atStartOfDay(ZoneId.systemDefault()).toInstant())));

            System.out.println(weekly.toString());


            Aggregation aggregation = newAggregation(
                    ridesCompleted,
                    weekly,
                    yearProjection
            );


            aggregate = mongoTemplate.aggregate(aggregation,
                    "incidents", Incident.class);
        }
        else if(routesTakenType.equals("monthly"))
        {
            AggregationOperation monthly = Aggregation.match(new Criteria("date")
                    .gte(Date.from(LocalDate.now().withDayOfMonth(1)
                            .atStartOfDay(ZoneId.systemDefault()).toInstant()))
                    .lt(Date.from(LocalDate.now()
                            .atStartOfDay(ZoneId.systemDefault()).toInstant())));




            Aggregation aggregation = newAggregation(
                    ridesCompleted,
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

    @RequestMapping(value = "/currentRoute/{ambulanceNo}")
    @ResponseBody
    public Incident getAmbulanceDetails(@PathVariable("ambulanceNo")
            String ambulanceNo) {
       Incident incident=incidentRepo.findByAmbulanceNoAndCurrentRoute(ambulanceNo,true);
        System.out.println(incident);
        if(incident==null){
            return null;
        }
        return incident;
    }
}
