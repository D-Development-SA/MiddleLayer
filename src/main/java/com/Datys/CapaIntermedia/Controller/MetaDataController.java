package com.Datys.CapaIntermedia.Controller;

import com.Datys.CapaIntermedia.Service.Implementations.MetaDataImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("v1")
public class MetaDataController {
    @Autowired
    private MetaDataImpl metaDataImpl;

    @GetMapping("/{index}")
    public ResponseEntity<Iterable<Object>> findAll(@PathVariable String index) throws IOException {
        return metaDataImpl.searchAllDocuments(index);
    }
    
    @GetMapping("/findById/{id}+{index}")
    public ResponseEntity<Map<String, Object>> findById(@PathVariable String id, @PathVariable String index) throws IOException {
        System.out.println(id);
        System.out.println(index);
        return metaDataImpl.getDocumentById(id, index);
    }
    
    @PostMapping("/createOrUpdate/{index}+{id}")
    public ResponseEntity<Map<String, String>> create(@RequestBody Object e, @PathVariable String id, @PathVariable String index) throws IOException {
        return metaDataImpl.createOrUpdateDocument(e, index, id);
    }

    @DeleteMapping("/delete+{id}+{index}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable String id, @PathVariable String index) throws IOException {
        return metaDataImpl.deleteDocumentById(id, index);
    }
}
