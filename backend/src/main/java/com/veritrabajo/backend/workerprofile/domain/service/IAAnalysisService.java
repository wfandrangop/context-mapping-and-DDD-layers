package com.veritrabajo.backend.workerprofile.domain.service;

import com.veritrabajo.backend.workerprofile.domain.model.AnalysisResult;
import com.veritrabajo.backend.workerprofile.domain.model.RawDescription;

/**
 * Domain port for AI-assisted parsing of a worker's raw experience text.
 * Implementations live in infrastructure (ACL) so domain stays free of vendor specifics.
 */
public interface IAAnalysisService {

    /**
     * Extracts inferred occupations and technical skills from unstructured description text.
     *
     * @param description validated raw text supplied by the worker
     * @return structured analysis result
     */
    AnalysisResult analyze(RawDescription description);
}
