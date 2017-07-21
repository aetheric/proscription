package nz.co.aetheric.proscription.api

import nz.co.aetheric.proscription.WatcherService
import nz.sodium.Cell
import nz.sodium.CellSink
import java.io.File
import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.Path
import javax.inject.Inject

class TargetFile(val path: Path) {

	@Inject
	lateinit var watchers: WatcherService

	internal val file: File = path.toFile()

	/** Whether the file currently exists. */
	val exists: Cell<Boolean>

	/** The last time this file was modified by the filesystem */
	val modified: Cell<Long>

	/** The time this file was last updated by a proscription process. */
	val proscribed: Cell<Long>

	/** The content hash noted during the last proscription. */
	val hash: Cell<String>

	init {

		// Set-up private writable fields.
		val _exists = CellSink(file.exists())
		val _modified: CellSink<Long>
		val _proscribed: CellSink<Long>
		val _hash: CellSink<String>

		// Initialise the cell sinks.
		if (!_exists.sample()) {
			_modified = CellSink(0L)
			_proscribed = CellSink(0L)
			_hash = CellSink("")

		} else {
			val attrs = Files.readAttributes(path,
					"lastModifiedTime,proscriptionTime,proscriptionHash",
					LinkOption.NOFOLLOW_LINKS)
			_modified = CellSink(attrs["lastModifiedTime"] as Long? ?: 0L)
			_proscribed = CellSink(attrs["proscriptionTime"] as Long? ?: 0L)
			_hash = CellSink(attrs["proscriptionHash"] as String? ?: "")
		}


		watchers.on(path) { event ->

			if (event.name() == "ENTRY_DELETE") {
				_exists.send(false)
				_modified.send(0L)
				_proscribed.send(0L)
				_hash.send("")
				return@on
			}

			_exists.send(true)

			val attrs = Files.readAttributes(path,
					"lastModifiedTime,proscriptionTime,proscriptionHash",
					LinkOption.NOFOLLOW_LINKS)

			_modified.send(attrs["lastModifiedTime"] as Long? ?: 0L)
			_proscribed.send(attrs["proscriptionTime"] as Long? ?: 0L)
			_hash.send(attrs["proscriptionHash"] as String? ?: "")

		}

		// Set all the read-only fields.
		exists = _exists
		modified = _modified
		proscribed = _proscribed
		hash = _hash

	}

}

