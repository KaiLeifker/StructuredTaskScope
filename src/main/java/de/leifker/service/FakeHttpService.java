package de.leifker.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import de.leifker.dto.Infos;
import de.leifker.dto.Talk;
import lombok.SneakyThrows;

@Service
public class FakeHttpService {
	@SneakyThrows
	public Talk retrieveTalk(String name) {
		Thread.sleep(1_000);
		return new Talk("Talk für " + name);
	}

	@SneakyThrows
	public Infos retrieveInfoFromGoogle(String name) {
		System.out.println("Start Google: " + Instant.now());
		Thread.sleep(1000);
		System.out.println("Finish Google: " + Instant.now());
		return new Infos(List.of(name, "Google", "18.10.1994"));
	}

	@SneakyThrows
	public Infos retrieveInfoFromLinkedin(String name) {
		System.out.println("Start Linkedin: " + Instant.now());
		Thread.sleep(800);
		System.out.println("Finish Linkedin: " + Instant.now());
		return new Infos(List.of(name, "Linkedin", "18.10.1994", "Fullstack"));
	}

	@SneakyThrows
	public Infos retrieveInfoFromFacebook(String name) {
		System.out.println("Start Facebook: " + Instant.now());
		Thread.sleep(500);
		System.out.println("Finish Facebook: " + Instant.now());
		throw new RuntimeException("Keine Daten vorhanden");
	}

	@SneakyThrows
	public Infos retrieveInfoFromFacebookWorking(String name) {
		System.out.println("Start Facebook: " + Instant.now());
		Thread.sleep(500);
		System.out.println("Finish Facebook: " + Instant.now());
		return new Infos(List.of(name, "Facebook", "18.10.1994"));
	}
}
