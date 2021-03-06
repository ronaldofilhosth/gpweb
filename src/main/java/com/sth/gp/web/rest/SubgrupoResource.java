package com.sth.gp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.sth.gp.domain.Subgrupo;
import com.sth.gp.repository.SubgrupoRepository;
import com.sth.gp.web.rest.util.HeaderUtil;
import com.sth.gp.web.rest.util.PaginationUtil;

/**
 * REST controller for managing Subgrupo.
 */
@RestController
@RequestMapping("/api")
public class SubgrupoResource {

    private final Logger log = LoggerFactory.getLogger(SubgrupoResource.class);

    @Inject
    private SubgrupoRepository subgrupoRepository;

    /**
     * POST  /subgrupos -> Create a new subgrupo.
     */
    @RequestMapping(value = "/subgrupos",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Subgrupo> createSubgrupo(@RequestBody Subgrupo subgrupo) throws URISyntaxException {
        log.debug("REST request to save Subgrupo : {}", subgrupo);
        if (subgrupo.getId() != null) {
            return ResponseEntity.badRequest().header("Falha", "Um novo Subgrupo não pode já ter um Código").body(null);
        }
        Subgrupo result = subgrupoRepository.save(subgrupo);        
        return ResponseEntity.created(new URI("/api/subgrupos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("subgrupo", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /subgrupos -> Updates an existing subgrupo.
     */
    @RequestMapping(value = "/subgrupos",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Subgrupo> updateSubgrupo(@RequestBody Subgrupo subgrupo) throws URISyntaxException {
        log.debug("REST request to update Subgrupo : {}", subgrupo);
        if (subgrupo.getId() == null) {
            return createSubgrupo(subgrupo);
        }
        Subgrupo result = subgrupoRepository.save(subgrupo);        
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("subgrupo", subgrupo.getId().toString()))
            .body(result);
    }

    /**
     * GET  /subgrupos -> get all the subgrupos.
     */
    @RequestMapping(value = "/subgrupos",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Subgrupo>> getAllSubgrupos(Pageable pageable)
        throws URISyntaxException {
        Page<Subgrupo> page = subgrupoRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/subgrupos");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /subgrupos/:id -> get the "id" subgrupo.
     */
    @RequestMapping(value = "/subgrupos/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Subgrupo> getSubgrupo(@PathVariable Long id) {
        log.debug("REST request to get Subgrupo : {}", id);
        return Optional.ofNullable(subgrupoRepository.findOne(id))
            .map(subgrupo -> new ResponseEntity<>(
                subgrupo,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /subgrupos/:id -> delete the "id" subgrupo.
     */
    @RequestMapping(value = "/subgrupos/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteSubgrupo(@PathVariable Long id) {
        log.debug("REST request to delete Subgrupo : {}", id);
        subgrupoRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("subgrupo", id.toString())).build();
    }

    
    /**
     * SEARCH  /_search/grupos/:query -> search for the grupo corresponding
     * to the query.
     */
    @RequestMapping(value = "/_search/subgrupos/{query}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Subgrupo>> searchSubgrupos(@PathVariable String query, Pageable pageable)
        throws URISyntaxException {
    	
    	try{ 
    		
	    	String[] parameters = query.split(",");
	    	Page<Subgrupo> page; 
	    	
	    	if(parameters[0].equals("codigo")){    		
	    		page = subgrupoRepository.findById(Long.parseLong(parameters[1]), pageable);
	    	}else{
	    		page = subgrupoRepository.findByNmSubGrupoStartingWithOrderByNmSubGrupoAsc(parameters[1], pageable);
	    	}    	
	    	
	        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/_search/subgrupos");
	        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
	        
    	}catch(Exception e){
    		return ResponseEntity.badRequest().header("Falha", e.getMessage()).body(null);
    	}
    }
}
