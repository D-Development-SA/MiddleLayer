package com.Datys.CapaIntermedia.Service.Implementations;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import co.elastic.clients.elasticsearch.core.search.Hit;

import java.io.IOException;
import java.util.*;

@Repository
public class MetaDataImpl {

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    public ResponseEntity<Map<String,String>> createOrUpdateDocument(Object o, String index, String id) throws IOException {
        Map<String, String> resp;

        IndexResponse response = elasticsearchClient.index(i -> i
                .index(index)
                .id(id)
                .document(o)
        );

        if(response.result().name().equals("Created")){

            resp = insertInfoResponse(index, id, response, "Document has been successfully created.");

            return new ResponseEntity<>(resp, HttpStatus.CREATED);

        }else if(response.result().name().equals("Updated")){

            resp = insertInfoResponse(index, id, response, "Document has been successfully updated.");

            return new ResponseEntity<>(resp, HttpStatus.CREATED);

        }

        resp = insertInfoResponse(index, id, response, "Error while performing the operation.");

        return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
    }

    private static Map<String, String> insertInfoResponse(String index, String id, IndexResponse response, String txt) {
        Map<String, String> resp = new HashMap<>();

        resp.put("State name", response.result().name());
        resp.put("Info", txt);
        resp.put("Index", index);
        resp.put("Id", id);

        return resp;
    }
    public ResponseEntity<Map<String,Object>> getDocumentById(String id, String index) throws IOException{
        Object object;
        Map<String, Object> resp;

        GetResponse<Object> response = elasticsearchClient.get(g -> g
                        .index(index)
                        .id(id),
                Object.class
        );

        if (response.found()) {
            object = response.source();
            resp = insertInfoWithGetResponse(index, id, response, "Found");
            resp.put("Source", object);
        } else {
            resp = insertInfoWithGetResponse(index, id, response, "Not found");
            return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    private static Map<String, Object> insertInfoWithGetResponse(String index, String id, GetResponse<Object> response, String txt) {
        Map<String, Object> resp = new HashMap<>();

        resp.put("Meeting status", ""+response.found());
        resp.put("Info", txt);
        resp.put("Index", index);
        resp.put("Id", id);

        return resp;
    }

    public ResponseEntity<Map<String,String>> deleteDocumentById(String id, String index) throws IOException {

        DeleteRequest request = DeleteRequest.of(d -> d.index(index).id(id));
        DeleteResponse deleteResponse = elasticsearchClient.delete(request);
        Map<String, String> resp;

        if (Objects.nonNull(deleteResponse.result()) && !deleteResponse.result().name().equals("NotFound")) {

            resp = insertInfoWithDeleteResponse(
                    index,
                    id,
                    deleteResponse,
                    "Object with id " + deleteResponse.id() + " has been deleted."
            );

            return new ResponseEntity<>(resp, HttpStatus.OK);

        }

        resp = insertInfoWithDeleteResponse(
                index,
                id,
                deleteResponse, "Object with id " + deleteResponse.id()+" does not exist."
        );

        return new ResponseEntity<>(resp, HttpStatus.OK);

    }

    private static Map<String, String> insertInfoWithDeleteResponse(String index, String id, DeleteResponse response, String txt) {
        Map<String, String> resp = new HashMap<>();

        resp.put("Status name", ""+response.result().name());
        resp.put("Info", txt);
        resp.put("Index", index);
        resp.put("Id", id);

        return resp;
    }

    public ResponseEntity<Iterable<Object>> searchAllDocuments(String index) throws IOException {

        SearchRequest searchRequest =  SearchRequest.of(s -> s.index(index));
        SearchResponse<Object> searchResponse =  elasticsearchClient.search(searchRequest, Object.class);
        List<Hit<Object>> hits = searchResponse.hits().hits();
        List<Object> objectList = new ArrayList<>();

        for(Hit<Object> object : hits){

            objectList.add(object.source());

        }
        return new ResponseEntity<>(objectList, HttpStatus.OK);
    }
}