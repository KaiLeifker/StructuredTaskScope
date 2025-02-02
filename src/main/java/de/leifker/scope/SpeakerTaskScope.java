package de.leifker.scope;

import java.util.concurrent.StructuredTaskScope;

import de.leifker.dto.Infos;
import de.leifker.dto.Speaker;
import de.leifker.dto.SpeakerPart;
import de.leifker.dto.Talk;

public class SpeakerTaskScope extends StructuredTaskScope<SpeakerPart> {

	private volatile Talk talk;
	private volatile Infos infos;

	@Override
	protected void handleComplete(Subtask<? extends SpeakerPart> subtask) {
		// pattern Matching combined with sealed-classes and combined completeness check
		// (no default-case)
		switch (subtask.get()) {
		case Talk talk -> this.talk = talk;
		case Infos infos -> this.infos = infos;
		}
	}

	public Speaker getSpeaker() {
		return new Speaker(this.talk, this.infos);
	}

}
