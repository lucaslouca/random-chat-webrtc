export type Peer = {
  id: string
  name: string
  remove?: boolean
}

export type Room = {
  name: string
}

export type IceCandidate = {
  id: string
  target: string
  candidate: RTCIceCandidateInit
}

export type Offer = RTCSessionDescriptionInit & {
  /** the offerer */
  source: string
  /** the target of the offer */
  target: string
}

export type Answer = RTCSessionDescriptionInit & {
  /** the answerer */
  source: string
  /** the offerer */
  target: string
}

/** Sets or updates a peer's ID and display name */
export type IdentifyMessage = {
  type: 'identify'
  data: Peer
}

/** Sent by a peer to request a room to join. Also send by the server as a response */
export type RoomMessage = {
  type: 'room'
  data?: Room
}

export type JoinMessage = {
  type: 'join'
  data?: Room
}

/* Sent by the server to tell peer to invite its roommate */
export type InviteMessage = {
  type: 'invite'
  data: Peer
}

/** Sent by a peer to reject an offer */
export type RejectMessage = {
  type: 'reject'
  data: {
    source: Peer['id']
    target: Peer['id']
  }
}

/** Used to exchange ICE candidates */
export type IceCandidateMessage = {
  type: 'icecandidate'
  data: IceCandidate
}

/** Sent by a peer to offer to start a call */
export type OfferMessage = {
  type: 'offer'
  data: Offer
}

/** Sent by a peer to accept an offer */
export type AcceptMessage = {
  type: 'accept'
  data: Answer
}

/** Messages used for RTC signalling */
export type RtcMessage =
  | IdentifyMessage
  | RoomMessage
  | JoinMessage
  | InviteMessage
  | IceCandidateMessage
  | OfferMessage
  | AcceptMessage
  | RejectMessage

/** Broadcast by the server to announce peer additions or removals */
export type PeerMessage = {
  type: 'peer'
  data: Peer
}

/** Sent by the server when a peer connects */
export type ReadyMessage = {
  type: 'ready'
  version: string
  id: string
}

/** Sent by the server when a peer has successfully identified itself */
export type HelloMessage = {
  type: 'hello'
}

/** A chat message sent from one peer to another */
export type ChatMessage = {
  type: 'chat'
  data: string
}

/** A signal message sent from one peer to another to signal that it is typing or not */
export type TypingMessage = {
  type: 'typing'
  data: boolean
}

/** A disconnect message sent from one peer to another */
export type DisconnectMessage = {
  type: 'disconnect'
}

/** Messages sent between peers over a data channel */
export type ClientMessage = ChatMessage | TypingMessage | DisconnectMessage

/** A generic event handler method */
export type Handler<T = unknown> = (event: T) => void

/**
 * An event sent from the signaling server to a connected peer
 */
export type RtcEvent = {
  peeradded: Peer
  invite: Peer
  peerupdated: Peer
  peerremoved: Peer
  peerconnected: {
    stream: MediaStream
    peer: Peer
  }
  peerdisconnected: Peer['id']
  connected: undefined
  disconnected: undefined
  offer: Offer
  error: Error
  chat: {
    peer: Peer
    message: string
  }
  typing: {
    peer: Peer
    typing: boolean
  }
}

/** Valid event names, used for typing function parameters */
export type RtcEventName = keyof RtcEvent
export type NonDataRtcEventName = {
  [K in keyof RtcEvent]: RtcEvent[K] extends undefined ? K : never
}[keyof RtcEvent]
export type DataRtcEventName = {
  [K in keyof RtcEvent]: RtcEvent[K] extends undefined ? never : K
}[keyof RtcEvent]
