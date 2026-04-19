package com.realestate.manager.property.export;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExportArchiveRepo extends MongoRepository<ExportArchive, String> {

}