package com.hexagon.hrms.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hexagon.hrms.domain.Job;
import com.hexagon.hrms.mapper.JobMapper;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class JobService {

	private final JobMapper jobMapper;

	public List<Job> getAll() {
		return jobMapper.selectAll();
	}
}
