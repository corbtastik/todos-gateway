package io.corbs;

import com.sun.tools.javac.comp.Todo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient("todos-api")
public interface TodosAPIClient {

    // Create
    @RequestMapping(method = RequestMethod.POST, value="/todos/")
    Todo create(@RequestBody Todo todo);

    // Retrieve
    @RequestMapping(method = RequestMethod.GET, value="/todos/{id}")
    Todo retrieve(@PathVariable("id") Integer id);

    @RequestMapping(method = RequestMethod.GET, value="/todos/")
    List<Todo> retrieve();

    // Update
    @RequestMapping(method = RequestMethod.PATCH, value="/todos/{id}")
    Todo update(@PathVariable("id") Integer id, @RequestBody Todo todo);

    // Delete
    @RequestMapping(method = RequestMethod.DELETE, value="/todos/{id}")
    void delete(@PathVariable("id") Integer id);

    @RequestMapping(method = RequestMethod.DELETE, value="/todos/")
    void delete();

    // Non-Crud ops we want to expose
    @RequestMapping(method = RequestMethod.GET, value="/todos/limit")
    Integer limit();



}

