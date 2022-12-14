package com.backend.season_checker;

import java.text.DateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(path = "/")
@CrossOrigin
public class MainController {

  @Autowired
  private ConnectRepository userRepository;

  @GetMapping(path = "/all")
  @ResponseBody
  public Iterable<LebensmittelEntity> getAllUsers() {
    return userRepository.findAll();
  }

  @GetMapping(path = "/allOffSeason")
  @ResponseBody
  public ArrayList<Object> getAllOffSeason() {
    Iterable<LebensmittelEntity> allData = userRepository.findAll();
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMdd");
    LocalDateTime now = LocalDateTime.now();
    int heute = Integer.parseInt(dtf.format(now));
    ArrayList<Object> myList = new ArrayList<Object>();

    for (LebensmittelEntity currentEntry : allData) {
      int anfang = Integer.parseInt(currentEntry.getAnfangsDatum().replace("-", ""));
      int ende = Integer.parseInt(currentEntry.getEndDatum().replace("-", ""));
      boolean isUeberJahr = anfang > ende;
      if ((anfang > heute || ende < heute) && !(isUeberJahr && ((ende > heute) || (anfang < heute)))) {
        myList.add(currentEntry);
      }
    }
    return myList;
  }

  @GetMapping(path = "/allOnSeason")
  @ResponseBody
  public ArrayList<Object> getallOnSeason() {
    Iterable<LebensmittelEntity> allData = userRepository.findAll();
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMdd");
    LocalDateTime now = LocalDateTime.now();
    int heute = Integer.parseInt(dtf.format(now));
    boolean isUeberJahr = false;
    ArrayList<Object> myList = new ArrayList<Object>();

    for (LebensmittelEntity currentEntry : allData) {
      int anfang = Integer.parseInt(currentEntry.getAnfangsDatum().replace("-", ""));
      int ende = Integer.parseInt(currentEntry.getEndDatum().replace("-", ""));
      isUeberJahr = anfang > ende;
      if (isUeberJahr && ((ende > heute) || (anfang < heute))) {
        myList.add(currentEntry);
      }
      if (anfang < heute && ende > heute) {
        myList.add(currentEntry);
      }
    }
    return myList;
  }

  // @GetMapping(path = "/allOnSeason")
  // @ResponseBody
  // public Iterable<LebensmittelEntity> getAllOnSeason() {
  // DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMdd");
  // LocalDateTime now = LocalDateTime.now();
  // int heute = Integer.parseInt(dtf.format(now));
  // return userRepository.findAllOnSeason(heute);
  // };

  @GetMapping(path = "/updateFavoritTest/{id}/{isFavorit}")
  public void getUpdateFavTest(
      @PathVariable int id,
      @PathVariable boolean isFavorit) {

    userRepository.updateTrueFalse(isFavorit, id);
  };

  @GetMapping(path = "/updateFavorit/{id}/{isFavorit}")
  @ResponseBody
  public LebensmittelEntity getUpdateFav(
      @PathVariable int id,
      @PathVariable boolean isFavorit) {

    LebensmittelEntity entity = userRepository.getLebensmittelById(id);
    entity.setFavorit(isFavorit);

    return userRepository.save(entity);
  };

  @RequestMapping(path = "/addEntity/{name}/{anfangsDatum}/{endDatum}/{isFavorit}", method = RequestMethod.GET)
  @ResponseBody
  public String getTest(
      @PathVariable String name,
      @PathVariable String anfangsDatum,
      @PathVariable String endDatum,
      @PathVariable boolean isFavorit) {

    LebensmittelEntity entity = new LebensmittelEntity(name, anfangsDatum, endDatum, isFavorit);
    userRepository.save(entity);

    return "Entity erzeugt: "
        + name + " "
        + anfangsDatum + " "
        + endDatum + " "
        + isFavorit;
  }
}