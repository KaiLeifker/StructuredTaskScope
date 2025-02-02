package de.leifker.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Subtask.State;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.leifker.dto.Infos;
import de.leifker.dto.Speaker;
import de.leifker.scope.InfoTaskScope;
import de.leifker.scope.SpeakerTaskScope;
import de.leifker.service.FakeHttpService;
import lombok.SneakyThrows;

@RestController
@RequestMapping("/api")
public class MainController {

	@Autowired
	private FakeHttpService fakeHttpService;

	/**
	 * Shows how to use different expressions of StructuredTaskScope
	 * (ShotdownOnSuccess, ShutdownOnFailure, "Normal")
	 * 
	 * @param name
	 * @return
	 */
	@GetMapping("/fast/java-class/infos/{name}")
	Infos getInfosByJavaClass(@PathVariable String name) {
		return getAllAvailableInfos(name);
//		return getFastestInfos(name);
//		return getAllInfosOrException(name);
	}

	/**
	 * Uses a slow synchronous-approach
	 * 
	 * @param name
	 * @return
	 */
	@GetMapping("/slow/infos/{name}")
	Infos getInfosSynchron(@PathVariable String name) {
		return getSlowInfos(name);
	}

	/**
	 * Uses a self-implemented-version of StructuredTaskScope
	 * 
	 * @param name
	 * @return
	 */
	@GetMapping("/fast/own-class/infos/{name}")
	Infos getInfosByOwnClass(@PathVariable String name) {
		return getInfos(name);
	}

	/**
	 * Shows a self implemented version of StructuredTaskScope where multiple
	 * response-types are handled
	 * 
	 * @param name
	 * @return
	 */
	@SneakyThrows
	@GetMapping("/speaker/{name}")
	Speaker getSpeaker(@PathVariable String name) {
		try (var scope = new SpeakerTaskScope()) {
			scope.fork(() -> fakeHttpService.retrieveTalk(name));
			scope.fork(() -> getInfos(name));
			scope.join();
			return scope.getSpeaker();
		}
	}

	@SneakyThrows
	private Infos getAllAvailableInfos(String name) {
		try (var scope = new StructuredTaskScope<Infos>()) {
			Collection<Infos> results = new ConcurrentLinkedQueue<>();
			var googleResponse = scope.fork(() -> fakeHttpService.retrieveInfoFromGoogle(name));
			var linkedinResponse = scope.fork(() -> fakeHttpService.retrieveInfoFromLinkedin(name));
			var facebookResponse = scope.fork(() -> fakeHttpService.retrieveInfoFromFacebook(name));
			scope.join();
			if (googleResponse.state() == State.SUCCESS) {
				results.add(googleResponse.get());
			}
			if (linkedinResponse.state() == State.SUCCESS) {
				results.add(linkedinResponse.get());
			}
			if (facebookResponse.state() == State.SUCCESS) {
				results.add(facebookResponse.get());
			}
			return results.stream().max(Comparator.comparingInt(infos -> infos.info().size())).orElseThrow();
		}
	}

	@SneakyThrows
	private Infos getFastestInfos(String name) {
		try (var scope = new StructuredTaskScope.ShutdownOnSuccess<Infos>()) {
			scope.fork(() -> fakeHttpService.retrieveInfoFromGoogle(name));
			scope.fork(() -> fakeHttpService.retrieveInfoFromLinkedin(name));
			// Facebook is faster, but does not work => your response is linkedin
			scope.fork(() -> fakeHttpService.retrieveInfoFromFacebook(name));
			scope.join();
			return scope.result();
		}
	}

	@SneakyThrows
	private Infos getAllInfosOrException(String name) {
		try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
			Collection<Infos> results = new ConcurrentLinkedQueue<>();
			var googleResponse = scope.fork(() -> fakeHttpService.retrieveInfoFromGoogle(name));
			var linkedinResponse = scope.fork(() -> fakeHttpService.retrieveInfoFromLinkedin(name));
			var facebookResponse = scope.fork(() -> fakeHttpService.retrieveInfoFromFacebook(name));
//			var facebookResponse = scope.fork(() -> fakeHttpService.retrieveInfoFromFacebookWorking(name));
			scope.join();
			// Necessary, otherwise you get
			// java.lang.IllegalStateException: Result is unavailable or subtask did not
			// complete successfully
			scope.throwIfFailed();
			results.add(googleResponse.get());
			results.add(linkedinResponse.get());
			results.add(facebookResponse.get());
			return results.stream().max(Comparator.comparingInt(infos -> infos.info().size())).orElseThrow();
		}
	}

	@SneakyThrows
	private Infos getInfos(String name) {
		try (var scope = new InfoTaskScope()) {
			scope.fork(() -> fakeHttpService.retrieveInfoFromGoogle(name));
			scope.fork(() -> fakeHttpService.retrieveInfoFromFacebook(name));
			scope.fork(() -> fakeHttpService.retrieveInfoFromLinkedin(name));
			scope.join();
			return scope.bestInfos();
		}
	}

	@SneakyThrows
	private Infos getSlowInfos(String name) {
		Collection<Infos> results = new ArrayList<>();
		results.add(fakeHttpService.retrieveInfoFromGoogle(name));
		results.add(fakeHttpService.retrieveInfoFromFacebookWorking(name));
		results.add(fakeHttpService.retrieveInfoFromLinkedin(name));
		return results.stream().max(Comparator.comparingInt(infos -> infos.info().size())).orElseThrow();
	}

}