package com.driver.services;


import com.driver.EntryDto.BookTicketEntryDto;
import com.driver.model.Passenger;
import com.driver.model.Ticket;
import com.driver.model.Train;
import com.driver.repository.PassengerRepository;
import com.driver.repository.TicketRepository;
import com.driver.repository.TrainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TicketService {

    @Autowired
    TicketRepository ticketRepository;

    @Autowired
    TrainRepository trainRepository;

    @Autowired
    PassengerRepository passengerRepository;


    public Integer bookTicket(BookTicketEntryDto bookTicketEntryDto)throws Exception{
        Optional<Train> optionalTrain = trainRepository.findById(bookTicketEntryDto.getTrainId());
        if(!optionalTrain.isPresent()){
            throw new Exception("Invalid train id");
        }
        Train train = optionalTrain.get();
        int bookedseat = 0;
        List<Ticket> bookticket = train.getBookedTickets();
        for(Ticket ticket : bookticket){
            bookedseat += ticket.getPassengersList().size();
        }
        if(bookedseat+bookTicketEntryDto.getNoOfSeats() < train.getNoOfSeats()){
            throw new Exception("Less tickets are available");
        }
        String station[] = train.getRoute().split(",");
        boolean fromstation = false;
        boolean tostation = false;
        int x= 0 ;
        int y = station.length-1;
        while(x < y){
            if(station[x].equals(bookTicketEntryDto.getFromStation().toString())){
                fromstation = true;
                break;
            }
            x++;
        }
        while (x < y){
            if(station[y].equals(bookTicketEntryDto.getToStation().toString())){
                tostation = true;
                break;
            }
            y--;
        }
        if(fromstation == false || tostation == false){
            throw new Exception("Invalid stations");
        }
        List<Integer> ids = bookTicketEntryDto.getPassengerIds();
        List<Passenger> passengerList = new ArrayList<>();
        for(Integer integer : ids){
            passengerList.add(passengerRepository.findById(integer).get());
        }

        Ticket ticket = new Ticket();
        ticket.setPassengersList(passengerList);
        ticket.setTrain(train);
        ticket.setFromStation(bookTicketEntryDto.getFromStation());
        ticket.setToStation(bookTicketEntryDto.getToStation());

        int fare = 0;
        fare = bookTicketEntryDto.getNoOfSeats()*(y-x)*300;
        ticket.setTotalFare(fare);
        train.getBookedTickets().add(ticket);
        train.setNoOfSeats(train.getNoOfSeats()-bookTicketEntryDto.getNoOfSeats());

        Passenger passenger = new Passenger();
        passenger.getBookedTickets().add(ticket);
        trainRepository.save(train);
        passengerRepository.save(passenger);
        return ticketRepository.save(ticket).getTicketId();



        //Check for validity
        //Use bookedTickets List from the TrainRepository to get bookings done against that train
        // Incase the there are insufficient tickets
        // throw new Exception("Less tickets are available");
        //otherwise book the ticket, calculate the price and other details
        //Save the information in corresponding DB Tables
        //Fare System : Check problem statement
        //Incase the train doesn't pass through the requested stations
        //throw new Exception("Invalid stations");
        //Save the bookedTickets in the train Object
        //Also in the passenger Entity change the attribute bookedTickets by using the attribute bookingPersonId.
       //And the end return the ticketId that has come from db


    }
}
