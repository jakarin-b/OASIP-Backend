package sit.int221.sj1.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import sit.int221.sj1.checkexeption.Error;
import sit.int221.sj1.dtos.EventCategoryEditDTO;
import sit.int221.sj1.entities.Eventcategory;
import sit.int221.sj1.repositories.EventCategoryRepository;
import sit.int221.sj1.repositories.EventRepository;


import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EventCategoryService {
    @Autowired
    private EventCategoryRepository repository;
    @Autowired
    private EventRepository eventRepository;

    Map<String,String> checkMessage = new HashMap<>();

    private Eventcategory mapEventCategoryEdit(Eventcategory existingEventCategory, EventCategoryEditDTO updateEventCategory){
        existingEventCategory.setEventCategoryName(updateEventCategory.getEventCategoryName());
        existingEventCategory.setEventDuration(updateEventCategory.getEventDuration());
        existingEventCategory.setEventCategoryDescription(updateEventCategory.getEventCategoryDescription());
        return existingEventCategory;
    }
    private ResponseEntity errorResponse(HttpServletRequest request,HttpStatus status){
        ZoneId bangkokTimeZone = ZoneId.of("Asia/Bangkok");
        Instant currentTime = Instant.now();
        Error error = new Error(currentTime.atZone(bangkokTimeZone).toString(),status.value(),request.getRequestURI(),status,"Validate Error",checkMessage);
        return new ResponseEntity<>(error,error.getHttpStatus());
    }

    private boolean getResponseEntity(@RequestBody Eventcategory eventCategory, HttpServletRequest request) {
        checkMessage.clear();
        if (eventCategory.getId() == null) {
            checkMessage.put("category-null","must have category for save");
        }
        if(eventCategory.getEventCategoryName() == null || eventCategory.getEventCategoryName() == ""){
            checkMessage.put("category-name","Must have category name first before add.");
        }
        if(eventCategory.getEventCategoryName() != null && eventCategory.getEventCategoryName().length() > 100){
            checkMessage.put("category-name","Category name length is over.");
        }
        if(eventCategory.getEventCategoryName() != null){
            List<Eventcategory> nameList = repository.findByEventCategoryNameIgnoreCase(eventCategory.getEventCategoryName().trim());
            if(!nameList.isEmpty()){
                if(nameList.get(0).getId() != eventCategory.getId()){
                    checkMessage.put("category-name-duplicate","Category name require unique");
                    if((eventCategory.getEventCategoryName() != null && eventCategory.getEventCategoryName() != "") && nameList.get(0).getId() == eventCategory.getId() && nameList.get(0).getEventCategoryName().trim().compareTo(eventCategory.getEventCategoryName().trim()) == 0){
                        checkMessage.remove("category-name-duplicate");
                    }
                }
            }
        }
        if(eventCategory.getEventDuration() == null){
            checkMessage.put("category-duration","Must have category duration first before add.");
        }
        if(eventCategory.getEventDuration() != null&&(eventCategory.getEventDuration() < 1 || eventCategory.getEventDuration() > 480)){
            checkMessage.put("category-duration","Event category duration must between 1 to 480");
        }
        if(eventCategory.getEventCategoryDescription() != null && eventCategory.getEventCategoryDescription().length() > 500){
            checkMessage.put("category-description","Event category description length is over");
        }
        if(checkMessage.isEmpty()){
            return true;
        }else{
            return false;
        }
    }

    public List<Eventcategory> getEventCategories(){
        return repository.findAll();
    }
    public ResponseEntity getEventCategoryById(Integer id,HttpServletRequest request){
        if(!repository.findById(id).isEmpty()){
            return ResponseEntity.status(200).body(repository.findById(id));
        }else {
            checkMessage.clear();
            checkMessage.put("id","id not found");
            return errorResponse(request,HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity edit(EventCategoryEditDTO edit,Integer eventCategoryId, HttpServletRequest request){
        Eventcategory category = repository.findById(eventCategoryId).map(e -> mapEventCategoryEdit(e,edit)).get();
        if(getResponseEntity(category,request)){
            category.setEventCategoryName(category.getEventCategoryName().trim());
            return ResponseEntity.status(200).body(repository.saveAndFlush(category));
        }else {
            return errorResponse(request,HttpStatus.BAD_REQUEST);
        }
    }
    public ResponseEntity getAllEventByCategoryId(Integer id,HttpServletRequest request){
        if(!repository.findById(id).isEmpty()){
            return ResponseEntity.status(200).body(eventRepository.findAllByEventCategoryId(repository.findById(id).get()));
        }else {
            checkMessage.clear();
            checkMessage.put("id","id not found");
            return errorResponse(request,HttpStatus.NOT_FOUND);
        }
    }

}
