package nz.co.aetheric.proscription.api

class TargetFile(val path: Path) {

	/** Whether the file currently exists. */
	val exists: Bool

	/** The last time this file was modified by the filesystem */
	val modified: Timestamp

	/** The time this file was last updated by a proscription process. */
	val proscribed: Timestamp

	/** The content hash noted during the last proscription. */
	val hash: String

}

