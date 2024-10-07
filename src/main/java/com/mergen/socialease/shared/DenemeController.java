package com.mergen.socialease.shared;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DenemeController {

	@GetMapping("/secured")
	String securePath() {
		return "securePlace";
	}
}
