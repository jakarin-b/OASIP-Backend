package sit.int221.sj1.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sit.int221.sj1.dtos.EventCategoryEditDTO;
import sit.int221.sj1.entities.Eventcategory;
import sit.int221.sj1.repositories.EventCategoryRepository;
import sit.int221.sj1.services.EventCategoryService;


import javax.servlet.http.HttpServletRequest;
import java.util.List;


@RestController
@RequestMapping("/api/categories")
//@CrossOrigin("*")
public class EventCategoryController {
    @Autowired
    private EventCategoryService categoryService;

    @Autowired
    private EventCategoryRepository repository;

    @GetMapping("")
    public List<Eventcategory> getAllEventCategory(){
        return categoryService.getEventCategories();
    }

    @GetMapping("/{eventCategoryId}")
    public ResponseEntity getEventCategoryById(@PathVariable Integer eventCategoryId,HttpServletRequest request){
        return categoryService.getEventCategoryById(eventCategoryId,request);
    }

    @GetMapping("/{eventCategoryId}/events")
    public ResponseEntity getAllEventByCategoryId(@PathVariable Integer eventCategoryId,HttpServletRequest request){
        return categoryService.getAllEventByCategoryId(eventCategoryId,request);
    }

    @PutMapping("/{eventCategoryId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity update(@RequestBody EventCategoryEditDTO updateEventCategory, @PathVariable Integer eventCategoryId, HttpServletRequest request){
        return categoryService.edit(updateEventCategory,eventCategoryId,request);
    }


}