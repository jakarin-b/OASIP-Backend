package sit.int221.sj1.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import sit.int221.sj1.checkexeption.Error;
import sit.int221.sj1.dtos.EventAddDTO;
import sit.int221.sj1.dtos.EventDTO;
import sit.int221.sj1.dtos.EventEditDTO;
import sit.int221.sj1.entities.Event;
import sit.int221.sj1.repositories.EventCategoryRepository;
import sit.int221.sj1.repositories.EventRepository;
import org.modelmapper.ModelMapper;
import sit.int221.sj1.utils.ListMapper;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;



@Service
public class EventService {
    @Autowired
    private EventRepository repository;
    @Autowired
    private EventCategoryRepository categoryRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ListMapper listMapper;

    Map<String,String> checkMessage = new HashMap<>();

    private Event mapEventEdit(Event existingEvent, EventEditDTO updateEvent){
        existingEvent.setEventNotes(updateEvent.getEventNotes());
        existingEvent.setEventStartTime(updateEvent.getEventStartTime());
        return existingEvent;
    }

    private Event convertDTOToEntity(EventAddDTO addEvent){
        Event e = new Event();
        if(addEvent.getBookingName() != null)e.setBookingName(addEvent.getBookingName());
        if(addEvent.getBookingEmail() != null)e.setBookingEmail(addEvent.getBookingEmail());
        if(addEvent.getEventStartTime() != null)e.setEventStartTime(addEvent.getEventStartTime());
        if(addEvent.getEventDuration() != null)e.setEventDuration(addEvent.getEventDuration());
        if(addEvent.getEventNotes() != null)e.setEventNotes(addEvent.getEventNotes());
        if(addEvent.getEventCategoryId() != null)e.setEventCategoryId(categoryRepository.findById(addEvent.getEventCategoryId()).get());
        return e;
    }

    private ResponseEntity errorResponse(HttpServletRequest request,HttpStatus status){
        ZoneId bangkokTimeZone = ZoneId.of("Asia/Bangkok");
        Instant currentTime = Instant.now();
        Error error = new Error(currentTime.atZone(bangkokTimeZone).toString(),status.value(),request.getRequestURI(),status,"Validate Error",checkMessage);
        return new ResponseEntity<>(error,error.getHttpStatus());
    }

    public Boolean checkOverlapByDateTime(Event event) {
        List<Event> checkOverLap;
        if(event.getId() == null){
            checkOverLap = repository.findByEventStartTimeBetween(event.getEventStartTime().plus(Duration.ofSeconds(1)),event.getEventStartTime().plus(Duration.ofMinutes(event.getEventDuration())).minus(Duration.ofSeconds(1)),event.getEventCategoryId());
        }else {
            checkOverLap = repository.findByEventStartTimeBetweenAndIdNot(event.getEventStartTime().plus(Duration.ofSeconds(1)),event.getEventStartTime().plus(Duration.ofMinutes(event.getEventDuration())).minus(Duration.ofSeconds(1)),event.getEventCategoryId(),event.getId());
        }
        if (checkOverLap.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean checkValidateEmail(String email){
        String regexPattern = "^([a-zA-Z0-9._-])+@\\w+([a-zA-Z0-9._-])*(\\.[a-zA-Z0-9_-]{2,10})+$";
        return Pattern.compile(regexPattern).matcher(email).matches();
    }

    private boolean getResponseEntity(@RequestBody Event newEvent) {
        checkMessage.clear();
        if (newEvent.getEventCategoryId() == null) {
            checkMessage.put("category","must have category for save");
        }
        if(newEvent.getEventDuration() == null){
            checkMessage.put("duration","Duration should be not null.");
        }
        if(newEvent.getEventStartTime() == null || newEvent.getEventStartTime().toString() == ""){
            checkMessage.put("time","must have input time before save");
        }
        if(newEvent.getEventStartTime()!= null && newEvent.getEventDuration() != null && !checkOverlapByDateTime(newEvent)){
            checkMessage.put("time","time overlap");
        }
        if(newEvent.getEventStartTime()!= null && newEvent.getEventStartTime().compareTo(Instant.now()) == -1){
            checkMessage.put("time","Time is a past");
        }
        if(newEvent.getBookingEmail() == null || newEvent.getBookingEmail() == ""){
            checkMessage.put("email","must have input email before save");
        }
        if (newEvent.getBookingEmail() != null && !checkValidateEmail(newEvent.getBookingEmail())){
            checkMessage.put("email","Email is invalid");
        }
        if(newEvent.getBookingEmail() != null && newEvent.getBookingEmail().length() > 150){
            checkMessage.put("email","Email length is over");
        }
        if(newEvent.getBookingName() == null || newEvent.getBookingName() == ""){
            checkMessage.put("booking-name","Must have input booking name before save");
        }
        if(newEvent.getEventNotes()!=null && newEvent.getBookingName().length() > 100){
            checkMessage.put("booking-name","Booking name length is over.");
        }
        if(newEvent.getEventNotes()!=null && newEvent.getEventNotes().length() > 500){
            checkMessage.put("booking-note","Booking note length is over.");
        }
        if(checkMessage.isEmpty()){
            return true;
        }else{
            return false;
        }
    }
    public ResponseEntity save(EventAddDTO event,HttpServletRequest request){
        Event newEvent = convertDTOToEntity(event);
        if(getResponseEntity(newEvent)){
            return ResponseEntity.status(201).body(modelMapper.map(repository.saveAndFlush(newEvent),EventAddDTO.class));
        }else{
            return errorResponse(request,HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity edit(EventEditDTO updateEvent,Integer eventId,HttpServletRequest request){
        Event event = repository.findById(eventId).map(e -> mapEventEdit(e,updateEvent)).get();
        if(getResponseEntity(event)){
            return ResponseEntity.status(200).body(modelMapper.map(repository.saveAndFlush(event),EventEditDTO.class));
        }else{
            return errorResponse(request,HttpStatus.BAD_REQUEST);
        }
    }

    public List<EventDTO> getAllEvents(){
        List<Event> eventList = repository.findAll();
        return listMapper.mapList(eventList,EventDTO.class, modelMapper);
    }
    public ResponseEntity getEventById(Integer id,HttpServletRequest request){
        if(!repository.findById(id).isEmpty()){
            return ResponseEntity.status(200).body(modelMapper.map(repository.findById(id),EventDTO.class));
        }else {
            checkMessage.clear();
            checkMessage.put("id","id not found");
            return errorResponse(request,HttpStatus.NOT_FOUND);
        }
    }
    public ResponseEntity getCategoryByEvenId(Integer eventId,HttpServletRequest request){
        if(!repository.findById(eventId).isEmpty()){
            Event e = repository.findById(eventId).get();
            return ResponseEntity.status(200).body(e.getEventCategoryId());
        }else {
            checkMessage.clear();
            checkMessage.put("id","id not found");
            return errorResponse(request,HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity delete(Integer eventId, HttpServletRequest request){
        if(!repository.findById(eventId).isEmpty()){
            repository.deleteById(eventId);
            return ResponseEntity.status(200).body("delete id:"+ eventId+" success.");
        }else {
            checkMessage.clear();
            checkMessage.put("id","id not found");
            return errorResponse(request,HttpStatus.NOT_FOUND);
        }
    }



}

