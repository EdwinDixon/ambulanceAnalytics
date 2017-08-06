package com.analytics.ambulance.repos;

import com.analytics.ambulance.entities.Ambulance;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by lenovo on 29/7/17.
 */
@ComponentScan
@Repository
public interface AmbulanceRepo extends MongoRepository<Ambulance, String> {
  List<Ambulance> findByLocationNear(Point p, Distance d);
}