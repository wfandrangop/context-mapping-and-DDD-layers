package com.veritrabajo.backend.workerprofile.domain.service;

import com.veritrabajo.backend.workerprofile.domain.model.AnalysisResult;
import com.veritrabajo.backend.workerprofile.domain.model.RawDescription;

public interface IAAnalysisService {

    AnalysisResult analyze(RawDescription description);
}
