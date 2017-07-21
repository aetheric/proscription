package nz.co.aetheric.proscription

import java.nio.file.*
import java.nio.file.StandardWatchEventKinds.*

typealias PathEvent = WatchEvent.Kind<Path>

class WatcherService : Runnable, AutoCloseable {

	internal val watcher: WatchService = FileSystems.getDefault().newWatchService()
	internal val actions: MutableMap<Path, (WatchKey) -> Unit> = mutableMapOf()
	internal val entries: MutableMap<WatchKey, Path> = mutableMapOf()

	fun on(path: Path, action: (PathEvent) -> Unit)
			= on(path, arrayOf(ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY), action)

	fun on(path: Path, events: Array<PathEvent>, action: (PathEvent) -> Unit

	) {
		val folderPath = path.parent


		val key = path.parent.register(watcher, *events)
		entries[key] = path
	}

	override fun run() {

	}

	override fun close() {
		watcher.close()
	}

}
