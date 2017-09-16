package com.analytics.ambulance.repos;

import com.analytics.ambulance.entities.Ambulance;
import com.analytics.ambulance.entities.Incident;
import com.google.gson.JsonObject;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by lenovo on 27/8/17.
 */
@ComponentScan
@Repository
public interface IncidentRepo extends MongoRepository<Incident, String> {
 /*   List<Incident> findByLocationNear(Point p, Distance d);*/

    Incident findByAmbulanceNoAndCurrentRoute(String ambulanceNo,boolean CurrentRoute);

}
