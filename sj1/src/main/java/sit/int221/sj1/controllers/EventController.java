package sit.int221.sj1.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sit.int221.sj1.dtos.EventAddDTO;
import sit.int221.sj1.dtos.EventDTO;
import sit.int221.sj1.dtos.EventEditDTO;
import sit.int221.sj1.services.EventService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@RestController
@RequestMapping("/api/events")
//@CrossOrigin("*")

public class EventController {

    @Autowired
    private EventService service;

    @GetMapping("")
    public List<EventDTO> getAllEvent() {
        return service.getAllEvents();
    }

    @GetMapping("/{eventId}")
    public ResponseEntity getEventById(@PathVariable Integer eventId,HttpServletRequest request) {
        return service.getEventById(eventId,request);
    }
    @GetMapping("/{eventId}/categories")
    public ResponseEntity getCategoryByEvenId(@PathVariable Integer eventId, HttpServletRequest request) {
        return service.getCategoryByEvenId(eventId,request);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity create(@RequestBody EventAddDTO newEvent, HttpServletRequest request){
        return service.save(newEvent,request);
    }


    @DeleteMapping("/{evenId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity delete(@PathVariable Integer evenId, HttpServletRequest request) {
        return service.delete(evenId,request);
    }

    @PutMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity update(@RequestBody EventEditDTO updateEvent, @PathVariable Integer eventId, HttpServletRequest request){
        return service.edit(updateEvent,eventId,request);
    }
}

