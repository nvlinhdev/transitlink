package vn.edu.fpt.transitlink.storage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.fpt.transitlink.storage.entity.FileInfo;

import java.util.UUID;

public interface FileInfoRepository extends JpaRepository<FileInfo, UUID> {

}


