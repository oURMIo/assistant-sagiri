package com.dach.sagiri.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import com.dach.sagiri.property.FileProperty;
import com.dach.sagiri.property.dto.Project;
import com.dach.sagiri.property.dto.UsefulUrl;

@Service
public class FileService {

    private final FileProperty fileProperty;

    public FileService(FileProperty fileProperty) {
        this.fileProperty = fileProperty;
    }

    public Optional<List<UsefulUrl>> getUsefulUrls() {
        return fileProperty.getUsefulUrls();
    }

    public Optional<List<Project>> getProjectList() {
        return fileProperty.getProjectList();
    }
}
