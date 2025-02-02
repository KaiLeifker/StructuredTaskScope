package de.leifker.service;

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
		Thread.sleep(20);
		return new Infos(List.of(name, "Google", "18.10.1994"));
	}

	@SneakyThrows
	public Infos retrieveInfoFromLinkedin(String name) {
		Thread.sleep(80);
		return new Infos(List.of(name, "Linkedin", "18.10.1994", "Fullstack"));
	}

	@SneakyThrows
	public Infos retrieveInfoFromFacebook(String name) {
		Thread.sleep(50);
		throw new RuntimeException("recieved no information");
	}

	@SneakyThrows
	public Infos retrieveInfoFromFacebookWorking(String name) {
		Thread.sleep(50);
		return new Infos(List.of(name, "Facebook", "18.10.1994"));
	}
}
