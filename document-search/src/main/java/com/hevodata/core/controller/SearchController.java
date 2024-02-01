package com.hevodata.core.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.hevodata.core.search.GoogleDriveSearchService;

/**
 * This is Search API which has been built on Elastic Search and Google Drive.
 * Only content search is supported in this
 * You can search any content from document it will list you File details
 * Response contains
 * {
 * 		Path : driverootId/FolderId/fileid
 * 		Extension : file extension
 * 		FileName: Name of the File.
 * }
 * */
@RestController
@RequestMapping("/api")
public class SearchController {
	
	private GoogleDriveSearchService service = new GoogleDriveSearchService();

    @GetMapping("/googleDrive/search")
    public @ResponseBody List<Map<String, Object>> googleDriveSearch(@RequestParam String q) {
    	List<Map<String,Object>> results = service.search(q);
        return results;
    }
}