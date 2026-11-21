package com.driftingleaf.seshasya.data

data class Task(val id: String, val title: String, val hint: String, val link: String? = null)
data class Block(val time: String, val title: String, val tasks: List<Task>)
data class Doctrine(
    val n: Int, val name: String, val figure: String, val devas: String,
    val text: String, val practice: String,
)
data class Deva(
    val name: String, val family: String, val meaning: String,
    val realm: String, val virtue: String, val honour: String,
)
data class Titled(val title: String, val text: String)
data class SwordForm(val situation: String, val form: String, val response: String)
data class Step(val title: String, val prompt: String, val hint: String)
data class Section(val id: String, val title: String, val subtitle: String)

object Content {

    // ---------------- Daily routine (chunked into six blocks: Miller's law) ----------------
    val routine = listOf(
        Block("First light", "Wake with the dawn", listOf(
            Task("earth", "Touch the earth", "Thank Dhara, who bears you; Sesha bears her."),
            Task("lamp", "Light a lamp", "For Anala, the eternal flame within."),
        )),
        Block("Morning", "Orientation", listOf(
            Task("creed", "Recite the Creed", "Om Anantaya Namah… Tat tvam asi. Avi yahan hai.", "library/creed"),
            Task("mantra", "Chant one mala (108)", "Om Anantaya Namah, or the Gayatri.", "sit?tab=1"),
            Task("doctrine", "Choose today's doctrine", "See “Today's light” above."),
        )),
        Block("Sitting", "Stillness", listOf(
            Task("sit", "Sit 10–20 minutes", "Breathe in: Tat tvam asi. Breathe out: Avi yahan hai.", "sit?tab=0"),
        )),
        Block("The day", "Application", listOf(
            Task("records", "Read the Records before a decision", "Find first principles; let the Sword choose.", "ledger?tab=1"),
            Task("speech", "True, kind, helpful, timely speech", "Ask the four questions before speaking."),
        )),
        Block("Evening", "Reflection", listOf(
            Task("mirror", "Mirror review", "What helped? What harmed? Ask forgiveness for any wrong."),
            Task("hara", "Give one burden to Hara", "Name it, then set it down."),
        )),
        Block("Night", "Rest", listOf(
            Task("ledger", "Write the Ananta ledger", "Effort · Growth · Remainder.", "ledger?tab=0"),
            Task("metta", "Loving-kindness", "Self, someone dear, someone neutral, someone difficult, all beings."),
        )),
    )
    val allTaskIds: List<String> = routine.flatMap { b -> b.tasks.map { it.id } }

    // ---------------- Seshan week (index 0 = Sunday) ----------------
    val week = listOf(
        "Sunday · Vivasvan and the Adityas: greet the sun; rightful pride and gratitude.",
        "Monday · Soma and the Rudras: healing, joy and release.",
        "Tuesday · Courage and Perseverance: face something difficult.",
        "Wednesday · Study: read from any sacred text.",
        "Thursday · Teachers and elders: thank a teacher; mentor someone.",
        "Friday · Lakshmi and the Vasus: generosity, beauty, care for nature.",
        "Saturday · Ananta Sesha: rest, reflection and the equilibrium review.",
    )

    // ---------------- Creed ----------------
    const val creedLine =
        "Ananta Sesha is the endless remainder; therefore possibility is infinite, growth is limitless, ascension is boundless, and eternal effort is its own reward."

    val tenets = listOf(
        Titled("I · Infinite Possibilities",
            "What remains after everything passes is always full. No life is used up and no future is closed.\n\nDaily: never say “there is nothing left for me.” Look for what remains, and begin from there."),
        Titled("II · Limitless Growth",
            "A person becomes what they resolve to be. Knowledge, skill, character and love can always grow.\n\nDaily: measure yourself against who you were, not against others. Rest is part of growth."),
        Titled("III · Boundless Ascension",
            "From the unreal to the real, from darkness to light, from death to immortality — with no ceiling. A Seshan ascends by bearing others, never by climbing over them.\n\nDaily: take one step higher, and lift someone as you go."),
        Titled("IV · Eternal Effort = Reward",
            "No sincere effort is ever lost. Effort is rewarded, and effort is itself the reward.\n\nDaily: keep moving. Charaiveti. Tune effort like a lute string — not too tight, not too loose."),
    )

    const val recitation =
        "Om Anantaya Namah.\nI honour the Endless, who remains when all has passed.\nFrom the full, the full; my possibilities are without end.\nI will grow without limit and rise without ceiling,\nbearing others as Sesha bears the worlds.\nNo sincere effort of mine is lost;\nmy striving is rewarded, and my striving is my reward.\nTat tvam asi. Avi yahan hai."

    val sayings = listOf(
        Titled("Tat Tvam Asi · तत् त्वम् असि",
            "“That you are.” The Endless is not foreign to you; it is your own deepest self — and the deepest self of every being. Like salt dissolved in water: unseen, yet everywhere. Like the banyan hidden in a seed too small to see.\n\nWhen you feel small, remember: Tat tvam asi."),
        Titled("Avi Yahan Hai · अभी यहाँ है",
            "“It is here, now.” The Endless is not far away and not later. It is present in this place and this breath. Effort can only be made now; the master removes the distance instead of crossing it.\n\nWhen the goal feels far away, remember: Avi yahan hai."),
        Titled("Living the sayings",
            "Breath: in with “Tat tvam asi,” out with “Avi yahan hai.”\nGreeting: one says “Tat tvam asi,” the other answers “Avi yahan hai.”\nSafeguard: “That you are” never means “I may do as I please.” Every being shares the same sacred worth."),
    )

    // ---------------- Sixteen doctrines ----------------
    val doctrines = listOf(
        Doctrine(1, "Perseverance", "Ganesha", "Dhata, Jayanta, Ahas",
            "Keep going through difficulty, delay and discouragement — but stay open to wisdom and change the method when it fails.",
            "Return to a worthy task after every setback."),
        Doctrine(2, "Patience & Kindness", "Krishna", "Mitra, Dhara",
            "Patience gives time for understanding and repair. Kindness governs how we treat others while we wait or hold power.",
            "Pause before reacting; let others explain themselves."),
        Doctrine(3, "Forgiveness", "Vishnara (Vishnu)", "Varuna, Vishnu",
            "Release the resentment that consumes you. Forgiveness does not deny harm or remove accountability.",
            "Release vengeance; keep memory, boundaries and safety."),
        Doctrine(4, "Integrity & Autonomy", "Shiva", "Ajaikapada, Dhruva, Nasatya",
            "Harmony between belief, word and deed; thinking for yourself without bowing to pressure.",
            "Say what you believe; change your mind for evidence, never for approval."),
        Doctrine(5, "Dignity", "Indra", "Indra",
            "The worth that governs how we treat ourselves and others. It refuses humiliation; it is not a demand to be served.",
            "Refuse to be humiliated, and never humiliate anyone."),
        Doctrine(6, "Potential", "Ananta Sesha", "Tvashtar",
            "What you can do today is not the limit of what you can become. A giant banyan grows from a seed too small to see.",
            "Learn deliberately; treat ability as something to grow."),
        Doctrine(7, "Chivalry & Reciprocation", "Parvati", "Aryaman",
            "Honour, courtesy, courage and restraint; answering generosity with generosity — never demanding it.",
            "Help without pressure; respond to freely given care."),
        Doctrine(8, "Openness", "Varuna", "Varuna, Virupaksha",
            "Meet new ideas and people without closing the mind too soon. Openness is not believing everything.",
            "Ask what evidence would change your mind."),
        Doctrine(9, "Rightful Pride & Pity", "Surya", "Vivasvan, Anala",
            "A worth born with the soul that cannot be put out — but your worth never makes another worth less. Pity means compassion, never contempt.",
            "Know your worth without lowering anyone else's."),
        Doctrine(10, "Assistance & Humility", "Chandra", "Pushan, Soma, Dasra",
            "Help where help is truly useful; know your limits without erasing yourself. Protect the dignity of the one helped.",
            "Offer useful help; admit what you do not know."),
        Doctrine(11, "Hunger", "Garuda", "Raivata, Anila",
            "A relentless desire to grow and achieve, governed by health, patience and ethics.",
            "Set meaningful goals; treat rest as part of achievement."),
        Doctrine(12, "Hope", "Lakshmi", "Bhaga, Savitra, Pratyusha",
            "Facing toward possibility when the outcome is uncertain. Joined with perseverance, hope becomes action.",
            "Name the next possible step, even in darkness."),
        Doctrine(13, "Temporal Insignificance", "Yama", "Tryambaka, Ahas",
            "One life is brief against the vastness of time. This shrinks vanity and makes our time precious.",
            "Remember time is limited; choose what matters."),
        Doctrine(14, "Negation", "Alexander von Corvus of the Black Sun", "Hara",
            "Refuse to be ruled by destructive emotional residue. Feel the first arrow; do not add a second. Forgive, but do not forget.",
            "Name each heavy feeling, examine it, set down what is not needed."),
        Doctrine(15, "Acceptance", "Horace of the Scorched Sun", "Amsha",
            "Accept what is true now without giving up on growth. Accept not being the best.",
            "Admire someone better than you, learn from them, keep improving."),
        Doctrine(16, "Enlightenment & Dedication", "The Buddha", "Savitr, Pinaki, Prabhasa",
            "Awakening to truth, and the lifelong practice of living it. Practice becomes firm when long, unbroken and loving.",
            "Study, reflect, test your understanding, keep going."),
    )

    // ---------------- Thirty-Three Devas ----------------
    val families = listOf("All", "Adityas", "Rudras", "Vasus", "Ashvins")
    val devas = listOf(
        Deva("Indra", "Adityas", "Lord, the mighty ruler", "King of the Devas; rain, thunder, courage", "Dignity", "Give thanks for rain; free what is trapped."),
        Deva("Aryaman", "Adityas", "The honourable companion", "Hospitality, marriage, ancestors", "Chivalry & Reciprocation", "Welcome a guest; offer water to ancestors."),
        Deva("Bhaga", "Adityas", "Share, good fortune", "Fortune and fair sharing", "Hope", "Rise before the sun; share good fortune."),
        Deva("Tvashtar", "Adityas", "The fashioner", "Craft, form, the making of bodies", "Potential", "Make or mend something slowly and well."),
        Deva("Varuna", "Adityas", "The encompasser", "Cosmic order, truth, oaths, waters", "Openness & Forgiveness", "Review the day honestly at evening; keep oaths."),
        Deva("Mitra", "Adityas", "Friend; contract", "Friendship, promises, daylight", "Patience & Kindness", "Make peace with someone; keep a promise."),
        Deva("Pushan", "Adityas", "The nourisher", "Roads, guidance, the lost", "Assistance & Humility", "Pray before a journey; help the lost."),
        Deva("Vivasvan (Surya)", "Adityas", "The brilliant", "The Sun; light and warmth", "Rightful Pride", "Greet the rising sun; sun salutation."),
        Deva("Savitr", "Adityas", "The impeller", "Awakening; inspiration of the mind", "Enlightenment & Dedication", "Recite the Gayatri at dawn and dusk."),
        Deva("Amsha", "Adityas", "Portion, share", "One's rightful portion", "Acceptance", "Give thanks before asking for more."),
        Deva("Dhata", "Adityas", "The establisher", "Order, creation, renewal", "Perseverance & Equilibrium", "Set foundations: one daily practice, one promise."),
        Deva("Vishnu", "Adityas", "The pervader", "Preservation; Lord of all; rests on Sesha", "Forgiveness & Boundless Ascension", "Om Namo Narayanaya; forgive one person."),
        Deva("Ajaikapada", "Rudras", "The unborn one-footed", "Lightning; the cosmic pillar", "Integrity & Autonomy", "Tree pose; one principle you never bend."),
        Deva("Ahirbudhnya", "Rudras", "Serpent of the deep", "The depths; the foundation of things", "The Drifting Leaf", "Honour beside Sesha; rest in deep stillness."),
        Deva("Virupaksha", "Rudras", "The one with unusual eyes", "Insight; the third eye", "Openness", "Before judging, ask what you are not seeing."),
        Deva("Raivata", "Rudras", "The abundant", "Abundance and prosperity", "Hunger", "Work for honest gain; give a portion away."),
        Deva("Hara", "Rudras", "The remover", "Removal of sorrow and burden", "Negation", "Each night, give one burden to Hara."),
        Deva("Bahurupa", "Rudras", "The many-formed", "Transformation; countless forms", "Infinite Possibilities", "Try something new each month."),
        Deva("Tryambaka", "Rudras", "The three-eyed", "Victory over death; healing", "Temporal Insignificance", "Recite the Death-Conquering Mantra for the ill."),
        Deva("Savitra", "Rudras", "Of the awakening sun", "The power to begin again", "Hope", "After any failure, start again next morning."),
        Deva("Jayanta", "Rudras", "The victorious", "Victory; completion", "Perseverance", "Finish one thing left undone."),
        Deva("Pinaki", "Rudras", "Bearer of the bow Pinaka", "The single decisive act", "Dedication", "One task at a time, with full attention."),
        Deva("Aparajita", "Rudras", "The unconquered", "Endurance", "Eternal Effort = Reward", "When you want to quit, take one more step."),
        Deva("Dhara", "Vasus", "The bearer", "Earth", "Patience & Kindness", "Touch the earth on waking; waste less."),
        Deva("Dhruva", "Vasus", "The fixed", "The Pole Star", "Integrity & Dedication", "Keep one commitment for a year."),
        Deva("Soma", "Vasus", "The nectar; the moon", "Moon, plants, healing, joy", "Assistance & Humility", "On Mondays and full moons, give thanks."),
        Deva("Ahas", "Vasus", "Day", "The day; the measure of time", "Perseverance", "Name what you will do with today."),
        Deva("Anila", "Vasus", "Wind, breath", "Wind and life-breath", "Hunger", "Slow breathing each morning; move daily."),
        Deva("Anala", "Vasus", "Fire", "Fire, purification, the inner flame", "The Eternal Flame", "Light a lamp each morning."),
        Deva("Pratyusha", "Vasus", "The first light", "Early dawn; new beginnings", "Hope", "Use the first quiet hour for practice."),
        Deva("Prabhasa", "Vasus", "Radiance", "The luminous sky", "Enlightenment", "Make one vow and keep it."),
        Deva("Nasatya", "Ashvins", "Never untrue", "Truth, rescue, safe return", "Integrity", "When you promise help, give it."),
        Deva("Dasra", "Ashvins", "The wonder-worker", "Healing and medicine", "Assistance", "Visit the sick; learn a healing skill."),
    )

    // ---------------- Dogma ----------------
    val dogma = listOf(
        "The Endless. Ananta Sesha is the Endless Remainder, bed of the Lord and bearer of all worlds.",
        "Infinite Possibilities. Because the Remainder is always full, no life is used up and no future is closed.",
        "Limitless Growth. No achievement is the final limit of a soul's development.",
        "Boundless Ascension. The soul may rise without ceiling, but only by bearing others.",
        "Eternal Effort = Reward. No sincere effort is lost; effort is rewarded and is itself the reward.",
        "The Lord and the Thirty-Three. Vishnu, resting on Sesha, is Lord of all; the Devas are his powers.",
        "The Leaf and the Sword. The leaf follows the wind, yet the sword determines where it falls.",
        "Wood and Sword. Wood creates what the sword must protect; the sword cuts away what prevents growth.",
        "No Distance. Remove the distance between intention and reality.",
        "Balance Over Strength. Whoever destroys the balance of nature to win has lost.",
        "Conceal Yourself as a Leaf. Seek no fame; let virtue be quiet.",
        "Draw the Sword Only Once. The best battle is the one prevented.",
        "The Serpent Brothers. Sesha bears the worlds; Vasuki adorns Shiva. Foundation and endurance are sacred.",
        "The Records and the Sword. Read the Akashic Records for first principles; let the Sword of Akasha choose the least force that is enough.",
        "The Two Great Sayings. Tat Tvam Asi — That you are. Avi Yahan Hai — It is here, now.",
        "Root, Wind, Leaves, Sword. Root yourself in Earth. Move like the wind. Conceal yourself among ten thousand leaves. With one sword, sever the void.",
    )

    // ---------------- Vows ----------------
    val vows = listOf(
        "I will honour Ananta Sesha, the Endless Remainder, as the centre of my path.",
        "I will treat no ending as final, for the remainder is always full of possibility.",
        "I will grow without limit and rise without ceiling, bearing others rather than climbing over them.",
        "I will strive eternally, trusting that no sincere effort is lost and that striving is itself the reward.",
        "I will cultivate perseverance without confusing it with stubbornness.",
        "I will practise patience and kindness without surrendering discernment.",
        "I will forgive without denying memory, accountability or boundaries.",
        "I will preserve integrity and autonomy while remaining teachable.",
        "I will uphold dignity in myself and recognise it in others.",
        "I will cultivate my potential without measuring my worth only by achievement.",
        "I will practise chivalry and reciprocation without entitlement.",
        "I will remain open without abandoning judgment.",
        "I will honour the soul's inherent worth without using pride to degrade another.",
        "I will assist with humility and respect the agency of others.",
        "I will keep hunger for growth while accepting that I need not be the best.",
        "I will sustain hope through action.",
        "I will remember time's limits and use life meaningfully.",
        "I will release needless emotional burdens without denying my humanity.",
        "I will accept what is true today and work toward what may become possible tomorrow.",
        "I will seek Sesha's embrace as a Drifting Leaf, while honouring the life and responsibilities before me.",
    )

    // ---------------- Chants (also used by the japa counter) ----------------
    val chants = listOf(
        Titled("Om Anantaya Namah", "“Salutations to the Endless.” The chant of the Drifting Leaf Pavilion."),
        Titled("Om Namo Narayanaya", "Salutations to Narayana, who rests on Sesha."),
        Titled("Tat tvam asi · Avi yahan hai", "“That you are · It is here, now.” The Two Great Sayings."),
        Titled("Om purnam adah purnam idam", "“That is full, this is full.” The chant of Infinite Possibilities."),
        Titled("Asato ma sad gamaya", "“Lead me from the unreal to the real, from darkness to light, from death to immortality.”"),
        Titled("The Gayatri", "Meditation on Savitr's radiance; asks for clear understanding."),
        Titled("Om Tryambakam yajamahe", "The Death-Conquering Mantra, for health and peace."),
        Titled("Charaiveti, charaiveti", "“Keep moving, keep moving.” The watchword of Eternal Effort."),
        Titled("Gate gate paragate", "“Gone, gone, gone beyond.” The chant of going beyond."),
    )
    val japaMantras = listOf(
        "Om Anantaya Namah", "Om Namo Narayanaya", "Tat tvam asi · Avi yahan hai",
        "Om Tryambakam yajamahe", "Charaiveti",
    )

    // ---------------- Safeguards ----------------
    val safeguards = listOf(
        "Pride is not permission to look down on anyone.",
        "Pity is not contempt; it means compassion.",
        "Autonomy is not refusing correction.",
        "Negation is not denying your feelings. If distress becomes overwhelming, reaching out to trusted people or professionals is strength.",
        "Forgiveness is not forced reconciliation.",
        "Acceptance is not accepting abuse or injustice.",
        "Chivalry is not entitlement.",
        "Surrender is to the Lord, never to a human demand for money, secrecy, isolation or obedience to wrong.",
        "The sword is discernment. The Pavilion does not teach violence against people.",
        "Eternal effort is not self-destruction. Sleep, health and rest are part of the path.",
        "Reward is not a verdict on suffering. Never tell anyone they deserved their pain.",
        "Ascension is not superiority.",
        "No one may claim to read another's Akashic Records to frighten, control or charge them.",
    )

    // ---------------- Sword of Akasha: daily forms ----------------
    val swordForms = listOf(
        SwordForm("Someone is confused", "Nandaka", "Explain clearly and kindly; cut the ignorance, not the person."),
        SwordForm("Someone is crossing a line", "Varuna's Pasha", "Set a firm, calm boundary that holds without harm."),
        SwordForm("Something is stuck", "Vajra", "Decide, and act at once."),
        SwordForm("The ground is not ready", "Balarama's plough", "Prepare patiently before planting."),
        SwordForm("The situation keeps changing", "Ruyi Jingu Bang", "Adapt to its scale."),
        SwordForm("Someone weaker is threatened", "Mjölnir", "Protect them."),
        SwordForm("You have failed", "Gram", "Reforge yourself stronger."),
        SwordForm("You hold power", "Excalibur", "Remember it is held in trust, and give it back."),
        SwordForm("You are angry", "Sword of Achala", "Cut the anger first, not the person."),
        SwordForm("An aim must be true", "Gungnir", "Aim once, carefully, and keep your vow."),
        SwordForm("Danger must become a way out", "Kusanagi", "Turn the threat into an escape route."),
        SwordForm("Nothing truly needs doing", "The Resting Sword", "Do nothing, and be at peace."),
    )

    // ---------------- Reading the Records: six steps ----------------
    val recordSteps = listOf(
        Step("Still the wind", "What question or decision are you facing? Take three slow breaths first — a troubled mind cannot read.", "My question is…"),
        Step("Read what has been", "What is actually known? What has been tried before, by you and by others?", "What happened, and why…"),
        Step("Say “not this”", "List each belief about the problem that is habit, fear or custom rather than essential. Set it aside.", "Not this: …"),
        Step("Find the remainder", "Name the few truths that cannot be removed. These are the first principles.", "What must be true…"),
        Step("Read what could be", "Starting only from the first principles, list every possible path — even unusual ones.", "Possible paths…"),
        Step("Draw the sword", "What does the situation truly need? Choose the form, then the one action you will take.", "The one action I will take…"),
    )

    // ---------------- Calendar ----------------
    val festivals = listOf(
        Titled("New Year (Chaitra)", "Honour Dhata. Set the year's foundations."),
        Titled("Rama Navami", "Honour Lakshmana, Sesha's descent."),
        Titled("Vesak", "Renew Enlightenment & Dedication."),
        Titled("Guru Purnima", "Honour all teachers and the Founder."),
        Titled("Nag Panchami", "Honour the Serpent Brothers, Sesha and Vasuki."),
        Titled("Janmashtami & Balarama Jayanti", "Honour Krishna and Balarama."),
        Titled("Ananta Chaturdashi", "The highest holy day. Tie the fourteen-knotted Ananta thread; renew all twenty vows."),
        Titled("Navaratri & Vijayadashami", "Honour the Goddess and Aparajita; begin a new study."),
        Titled("Diwali / Kartika Deepam", "Festival of lamps; honour the eternal flame."),
        Titled("Vaikuntha Ekadashi", "Meditate on Boundless Ascension."),
        Titled("Winter solstice", "Light returns from below; honour Hope."),
        Titled("Maha Shivaratri", "Night vigil for Shiva and the Eleven Rudras."),
    )

    // ---------------- Library index (kept to nine entries: Hick's law) ----------------
    val library = listOf(
        Section("creed", "The Central Creed", "Ananta Sesha and the four tenets"),
        Section("sayings", "The Two Great Sayings", "Tat Tvam Asi · Avi Yahan Hai"),
        Section("doctrines", "The Sixteen Doctrines", "Branches of the Creed"),
        Section("devas", "The Thirty-Three Devas", "Adityas, Rudras, Vasus, Ashvins"),
        Section("dogma", "The Pavilion Dogma", "Sixteen articles of faith"),
        Section("sword", "The Sword of Akasha", "The right response for each moment"),
        Section("vows", "The Twenty Vows", "Renewed each Ananta Chaturdashi"),
        Section("chants", "Sacred Chants", "Mantras and watchwords"),
        Section("calendar", "Week & Year", "Seshan week and festivals"),
        Section("safeguards", "Safeguards", "So the teaching is never twisted"),
    )
}
