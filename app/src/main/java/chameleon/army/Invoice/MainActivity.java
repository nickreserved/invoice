package chameleon.army.Invoice;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/** Η μια και μοναδική (κύρια) "δραστηριότητα" του προγράμματος. */
public class MainActivity extends Activity {
	/** Λίστα με τις τιμές του επιλογέα ΦΕ. */
	static private final Object[] INCOME_TAX_DATA = { 4, 8, 3, 1, 10, 0 };
	/** Λίστα με τις τιμές του επιλογέα ΦΠΑ. */
	static private final Integer[] VAT_DATA = { 24, 13, 6,    17, 9, 5,     0 };

	/** Λίστα με όλες τις κρατήσεις στο Στρατό. */
	//                             ΜΤΣ,     ΕΛΟΑΣ,   Χαρτόσημο,ΟΓΑ,      ΕΑΔΗΣΥ,ΒΑΜ,  ΕΚΟΕΜΣ
	final static private Hold[] holdListArmy = {
	// ΤΑΚΤΙΚΟΣ Π/Υ
	// Καθαρή αξία < 1000
	/*0*/	new Hold(new double[] {0.04,    0.02,    0.0012,   0.00024}),						// 6.144 - Μισθώματα ακινήτων
	/*1*/	new Hold(new double[] {0.04,    0.02,    0.0032,   0.00064}),						// 6.384 - Αμοιβές μελετητών
	// Καθαρή αξία >= 1000
	/*2*/	new Hold(new double[] {0.04,    0.02,    0.00123,  0.000246, 0.001}), 				// 6.2476 - ΟΧΙ Μισθώματα ακινήτων
	/*3*/	new Hold(new double[] {0.04,    0.02,    0.00323,  0.000646, 0.001}), 				// 6.4876 - Αμοιβές μελετητών
	// Προμήθεια από στρατιωτική εκμετάλλευση εξυπηρέτησης προσωπικόυ, δημόσιες υπηρεσίες, ΝΠΔΔ, ανεξαρτήτως ποσού
	/*4*/	new Hold(new double[] {0.03904, 0.01952, 0.0012,   0.00024}),						// 6 - Προμήθεια από Πρατήριο ή ΝΠΔΔ
	// ΙΔΙΟΙ ΠΟΡΟΙ
	// Καθαρή αξία < 1000
	/*5*/	new Hold(new double[] {0.04,	0.02,    0.0012,   0.00024,  0,	    0.02, 0.08}),	// 16.144 - Μισθώματα ακινήτων
	/*6*/	new Hold(new double[] {0.04,    0.02,    0.0032,   0.00064,  0,	    0.02, 0.08}),	// 16.384 - Αμοιβές μελετητών
	// Καθαρή αξία >= 1000
	/*7*/	new Hold(new double[] {0.04,    0.02,    0.00123,  0.000246, 0.001, 0.02, 0.08}), 	// 16.2476 - ΟΧΙ Μισθώματα ακινήτων
	/*8*/	new Hold(new double[] {0.04,    0.02,    0.00323,  0.000646, 0.001, 0.02, 0.08}), 	// 16.4876 - Αμοιβές μελετητών
	// Προμήθεια από στρατιωτική εκμετάλλευση εξυπηρέτησης προσωπικόυ, δημόσιες υπηρεσίες, ΝΠΔΔ, ανεξαρτήτως ποσού
	/*9*/	new Hold(new double[] {0.03904, 0.01952, 0.0012,   0.00024,  0,     0.02, 0.08}),	// 16 - Προμήθεια από Πρατήριο ή ΝΠΔΔ
	// Π/Υ ΠΔΕ
	// Καθαρή αξία < 1000
	/*10*/	new Hold(new double[] {0}),	                         			       				// 0 - Λογαριασμοί νερού, έργα ΔΕΗ, Μισθώματα ακινήτων
	/*11*/	new Hold(new double[] {0,       0,       0.002}),									// 0.2 - Αμοιβές μελετητών
	// Καθαρή αξία >= 1000
	/*12*/	new Hold(new double[] {0,       0,       0.00003,  0.000006, 0.001}), 				// 0.1036 - ΟΧΙ Μισθώματα ακινήτων
	/*13*/	new Hold(new double[] {0,       0,       0.00203,  0.000006, 0.001}), 				// 0.3036 - Αμοιβές μελετητών
	// ΔΑΠΑΝΕΣ ΛΕΣΧΩΝ
	/*14*/	new Hold(new double[] {0,       0,       0,       0,        0,     0.02, 0.08}), 	// 10 - Λέσχες
	};

	/** Λίστα με όλες τις κρατήσεις στην Αεροπορία. */
	//                             ΜΤΑ,  ΕΛΟΑΑ,Χαρτόσημο,ΟΓΑ,      ΕΑΔΗΣΥ
	final static private Hold[] holdListAirForce = {
	// Δαπάνες σε βάρος χρηματικών διαθεσίμων (π.χ. ώνια) και κερδών εκμεταλλεύσεων για λογαριασμό των εκμεταλλεύσεων
	// Λογαριασμοί νερού, έργα ΔΕΗ
	/*0*/	new Hold(new double[] {0}),										// 0 - Καθαρή αξία < 1000
	/*1*/	new Hold(new double[] {0,    0,    0.00003, 0.000006, 0.001}),	// 0.1036 - Καθαρή αξία >= 1000
	// Τακτικός προϋπολογισμός, δαπάνες σε βάρος εσωτερικών πόρων και κερδών εκμεταλλεύσεων
	// (όχι για λογαριασμό των εκμεταλλεύσεων) και δαπάνες σε βάρος αποθεματικών εκτός προϋπολογισμού
	/*2*/	new Hold(new double[] {0.04, 0.02, 0.0012,  0.00024,  0}),		// 6.144 - Καθαρή αξία < 1000
	/*3*/	new Hold(new double[] {0.04, 0.02, 0.00123, 0.000246, 0.001}),	// 6.2476 - Καθαρή αξία >= 1000
	};

	/** Το πιο χρησιμοποιημένο ΦΠΑ. */
	final static private int DEFAULT_VAT = 24;
	/** Το πιο χρησιμοποιημένο ΦΕ. */
	final static private int DEFAULT_INCOME_TAX = 4;

	/** Κλειδί αποθήκευσης του είδους του προμηθευτή. */
	static private final String CONTRACTOR = "Προμηθευτής";
	/** Κλειδί αποθήκευσης του ποσοστού ΦΠΑ. */
	static private final String VAT = "ΦΠΑ";
	/** Κλειδί αποθήκευσης του ποσοστού ΦΕ. */
	static private final String INCOME_TAX = "ΦΕ";
	/** Κλειδί αποθήκευσης του ποσοστού Κρατήσεων. */
	static private final String HOLDS = "Κρατήσεις";
	/** Κλειδί αποθήκευσης του τύπου του τιμολογίου. */
	static private final String INVOICE_TYPE = "ΤύποςΤιμολογίου";
	/** Κλειδί αποθήκευσης του τύπου του ποσού. */
	static private final String AMOUNT_TYPE = "ΤύποςΠοσού";
	/** Κλειδί αποθήκευσης του ποσού. */
	static private final String AMOUNT = "Ποσό";
	/** Κλειδί αποθήκευσης για το αν είναι ενεργός ο αυτόματος υπολογισμός κρατήσεων και ΦΕ. */
	static private final String AUTOMATIC = "Αυτόματο";
	/** Κλειδί αποθήκευσης για τον τύπο της χρηματοδότησης. */
	static private final String FINANCING_TYPE = "Χρηματοδότηση";
	/** Κλειδί αποθήκευσης για το αν η δαπάνη αφορά έργο. */
	static private final String CONSTRUCTION = "Εγκαταστάσεις";
	/** Κλειδί αποθήκευσης για τον κλάδο των Ενόπλων Δυνάμεων. */
	static private final String ARM_TYPE = "Κλάδος";

	/** Ο χρήστης είναι της Πολεμικής Αεροπορίας. */
	private boolean airforce;

	// Αρχικοποίηση του Activity
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Φόρτωση δεδομένων, εντός try-catch για όσα δεδομένα αλλάζουν τύπο από έκδοση σε έκδοση
		// γιατί αυτό οδηγεί σε ClassCastException
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		double valHolds = 0;
		boolean valAirforce = false;
		try {
			valHolds = Double.longBitsToDouble(pref.getLong(HOLDS, 0));
			valAirforce = pref.getBoolean(ARM_TYPE, false);
		} catch(ClassCastException ignored) {}

		// Κοινός listener για spinner Κρατήσεων, ΦΠΑ και ΦΕ
		AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(final AdapterView<?> parentView, View selectedItemView, int position, long id) {
				final int other = parentView.getCount() - 1;
				if (other == position) {    // Η τελευταία επιλογή είναι "Άλλο"
					// Ένας EditText για να εισάγουμε το νέο ποσοστό κρατήσεων ή ΦΠΑ ή ΦΕ
					final EditText txtNum = new EditText(MainActivity.this);
					final boolean holds = parentView == findViewById(R.id.spHolds);
					int flags = InputType.TYPE_CLASS_NUMBER;
					if (holds) flags |= InputType.TYPE_NUMBER_FLAG_DECIMAL;
					txtNum.setInputType(flags);
					txtNum.setHint("%");

					new AlertDialog.Builder(MainActivity.this)
							.setTitle(R.string.newValue)
							.setView(txtNum)
							.setPositiveButton(android.R.string.ok, (dialog, whichButton) -> {
								try {
									// Μετατροπή της εισόδου σε αριθμό στο [0, 80]
									String s = txtNum.getText().toString();
									Number num;
									// Αν το παρακάτω if γίνει =_?_:_, τότε το αποτέλεσμα γίνεται cast σε Double!
									if (holds) num = Double.parseDouble(s) / 100;
									else num = Integer.parseInt(s);
									if (num.intValue() > 80) {    // Η είσοδος δεν είναι έγκυρος αριθμός
										parentView.setSelection(0);
										Toast.makeText(MainActivity.this, R.string.invalidValue, Toast.LENGTH_SHORT).show();
										return;
									}
									// Επιλέγει τον αριθμό αν ήδη υπάρχει στη λίστα, ειδάλλως τον εισάγει
									if (selectVal(parentView, num))
										Toast.makeText(MainActivity.this, R.string.existedValue, Toast.LENGTH_SHORT).show();
									else setIncomeTaxInfo(parentView);
								} catch (NumberFormatException ignored) {}
							})
							.setNegativeButton(android.R.string.cancel, (dialog, whichButton) -> {
								parentView.setSelection(0);
								((ArrayAdapter<?>) parentView.getAdapter()).notifyDataSetChanged();
								setIncomeTaxInfo(parentView);
							})
							.show();
				} else setIncomeTaxInfo(parentView);
			}
			@Override public void onNothingSelected(AdapterView<?> parentView) {}
		};

		// Πρόγραμμα σε κατάσταση Πολεμικής Αεροπορίας ή Ελληνικού Στρατού
		setMode(valAirforce);
		// Λήψη του αποθηκευμένου ΦΠΑ
		float valVatEuro;
		int valVatPercent;
		boolean euro;
		try {
			valVatPercent = pref.getInt(VAT, DEFAULT_VAT);
			euro = false; valVatEuro = 0;
		} catch(ClassCastException ex) {
			valVatEuro = pref.getFloat(VAT, 0);
			euro = true; valVatPercent = DEFAULT_VAT;
		}
		// Αρχικοποίηση spinner ΦΠΑ ποσοστού ή ευρώ
		Spinner spinner = findViewById(R.id.spVATUnit);
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(final AdapterView<?> parentView, View selectedItemView, int position, long id) {
				// Περιγραφή του ΦΠΑ
				((TextView) findViewById(R.id.tvVatInfo)).setText(position != 0 ? R.string.VATInfo2 : R.string.VATInfo1);
				// Εμφάνιση απόκρυψη του component του ποσοστού
				findViewById(R.id.spVAT).setVisibility(position != 0 ? View.GONE : View.VISIBLE);
				// Εμφάνιση απόκρυψη του component του ποσού σε ευρώ
				findViewById(R.id.txtVat).setVisibility(position == 0 ? View.GONE : View.VISIBLE);
				// Επανυπολογισμός
				calculation();
			}
			@Override public void onNothingSelected(AdapterView<?> parentView) {}
		});
		spinner.setSelection(euro ? 1 : 0);
		// Αρχικοποίηση spinner ποσοστού ΦΠΑ
		spinner = findViewById(R.id.spVAT);
		spinner.setAdapter(createAdapter(VAT_DATA));
		selectVal(spinner, valVatPercent);
		spinner.setOnItemSelectedListener(listener);
		// Αρχικοποίηση text field ποσού ΦΠΑ
		EditText editText = findViewById(R.id.txtVat);
		TextWatcher tw = new TextWatcher() {
			@Override public void afterTextChanged(Editable s) { calculation(); }
			@Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			@Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
		};
		editText.addTextChangedListener(tw);
		if (valVatEuro > 0) editText.setText(Float.toString(valVatEuro).replace(".0", ""));
		// Αρχικοποίηση spinner ΦΕ
		spinner = findViewById(R.id.spIncomeTax);
		spinner.setAdapter(createAdapter(INCOME_TAX_DATA));
		selectVal(spinner, pref.getInt(INCOME_TAX, DEFAULT_INCOME_TAX));
		spinner.setOnItemSelectedListener(listener);
		setIncomeTaxInfo(spinner);
		// Αρχικοποίηση spinner κρατήσεων (αρχικοποιείται στο setMode())
		spinner = findViewById(R.id.spHolds);
		selectVal(spinner, valHolds);
		spinner.setOnItemSelectedListener(listener);
		// Αρχικοποίηση spinner προμηθευτή
		spinner = findViewById(R.id.spContractorType);
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(final AdapterView<?> parentView, View selectedItemView, int position, long id) {
				// Περιγραφή του είδους του προμηθευτή
				((TextView) findViewById(R.id.tvContractorInfo)).setText(getResources().getStringArray(R.array.contractorInfo)[position]);
				// Αν δεν είναι ιδιώτης, δε χρειάζονται components για ΦΕ
				findViewById(R.id.layIncomeTax).setVisibility(position != 0 /* Όχι ιδιώτης */ ? View.GONE : View.VISIBLE);
				// Αν είναι στρατός, δε χρειάζονται components για ΦΠΑ
				findViewById(R.id.layVAT).setVisibility(position == 2 /* Στρατός */ ? View.GONE : View.VISIBLE);
				// Αν δεν είναι ιδιώτης, δε χρειάζονται components για έργο ΜΧ
				findViewById(R.id.cbConstruction).setVisibility(position != 0 /* Όχι ιδιώτης */ || airforce ? View.GONE : View.VISIBLE);
				// Αν είναι στρατός, δε χρειάζονται components για το είδος τιμολογίου
				findViewById(R.id.layInvoiceType).setVisibility(position == 2 /* στρατός */ ? View.GONE : View.VISIBLE);
				// Επανυπολογισμός
				calculation();
			}
			@Override public void onNothingSelected(AdapterView<?> parentView) {}
		});
		spinner.setSelection(pref.getInt(CONTRACTOR, 0));
		// Κοινός listener για τον τύπο της αξίας, του τιμολογίου, της χρηματοδότησης
		AdapterView.OnItemSelectedListener swListener = new AdapterView.OnItemSelectedListener() {
			@Override public void onItemSelected(final AdapterView<?> parentView, View selectedItemView, int position, long id) { calculation(); }
			@Override public void onNothingSelected(AdapterView<?> parentView) {}
		};
		// Αρχικοποίηση spinners του τύπου της αξίας
		spinner = findViewById(R.id.spAmountType);
		spinner.setOnItemSelectedListener(swListener);
		spinner.setSelection(pref.getInt(AMOUNT_TYPE, 0));
		// Αρχικοποίηση spinners του τύπου του τιμολογίου
		spinner = findViewById(R.id.spInvoiceType);
		spinner.setOnItemSelectedListener(swListener);
		spinner.setSelection(pref.getInt(INVOICE_TYPE, 0));
		// Αρχικοποίηση spinners του τύπου της χρηματοδότησης
		spinner = findViewById(R.id.spFinancingType);
		spinner.setOnItemSelectedListener(swListener);
		spinner.setSelection(pref.getInt(FINANCING_TYPE, 0));
		// Αρχικοποίηση edittext για την αξία
		editText = findViewById(R.id.txtAmount);
		editText.addTextChangedListener(tw);
		float b = pref.getFloat(AMOUNT, 0);
		if (b > 0) editText.setText(Float.toString(b).replace(".0", ""));
		// Αρχικοποίηση compound button για τον αυτόματο υπολογισμό
		CompoundButton checkBox = findViewById(R.id.swAuto);
		checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
			if (isChecked) {
				findViewById(R.id.layAdvanced).setVisibility(View.GONE);
				findViewById(R.id.layAutomatic).setVisibility(View.VISIBLE);
				// Το επεξηγηματικό κείμενο του κομβίου αυτόματου υπολογισμού
				((TextView) findViewById(R.id.tvAutoInfo)).setText(getString(
						airforce ? R.string.tvAutoOnAirForce : R.string.tvAutoOnArmy));
			} else {
				findViewById(R.id.layAdvanced).setVisibility(View.VISIBLE);
				findViewById(R.id.layAutomatic).setVisibility(View.GONE);
				((TextView) findViewById(R.id.tvAutoInfo)).setText(getString(R.string.tvAutoOff));
			}
			calculation();
		});
		checkBox.setChecked(pref.getBoolean(AUTOMATIC, true));
		// Αρχικοποίηση checkbox για έργο ΜΧ
		checkBox = findViewById(R.id.cbConstruction);
		checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> calculation());
		checkBox.setChecked(pref.getBoolean(CONSTRUCTION, false));
	}

	/** Επιλέγει μια τιμή σε έναν επιλογέα, αλλά αν δεν υπάρχει, την εισάγει πρώτα.
	 * Αφορά μόνο τους επιλογείς του ΦΕ, ΦΠΑ και κρατήσεων.
	 * @param spinner Ο επιλογέας
	 * @param num Η τιμή για εισαγωγή. Είναι integer αν πρόκεται για ΦΠΑ και ΦΕ και double αν
	 * πρόκειται για κρατήσεις. */
	static private boolean selectVal(AdapterView<?> spinner, Number num) {
		final int other = spinner.getCount() - 1;
		// Αν ο αριθμός ήδη υπάρχει στη λίστα...
		for (int z = 0; z < other; ++z)
			if (spinner.getItemAtPosition(z).equals(num)) {
				spinner.setSelection(z);	// ...τον επιλέγει
				return true;
			}
		// ...ειδάλλως τον εισάγει
		ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
		adapter.insert(num instanceof Double ? new Hold(num.doubleValue()) :  num, other);
		spinner.setSelection(other);	// ...τον επιλέγει
		adapter.notifyDataSetChanged();
		return false;
	}

	/** Θέτει τη λειτουργία του προγράμματος για Στρατό ή Αεροπορία.
	 * Τροποποιεί το spinner των κρατήσεων, το επεξηγηματικό κείμενο του αυτόματου υπολογισμού, το
	 * αν η δαπάνη είναι έργο ή όχι, τις κατηγορίες τιμολογίου και κάνει επανυπολογισμό.
	 * @param airforce Το πρόγραμμα τίθεται σε λειτουργία Αεροπορίας */
	private void setMode(boolean airforce) {
		// Τίθεται η μεταβλητή
		this.airforce = airforce;
		// Οι συνέπειες της μεταβλητής
		Hold[] holdList = airforce ? holdListAirForce : holdListArmy;
		ArrayAdapter adapter = createAdapter(holdList);
		Spinner spinner = findViewById(R.id.spHolds);
		spinner.setAdapter(adapter);
		// Το επεξηγηματικό κείμενο του κομβίου αυτόματου υπολογισμού
		if (isAuto())
			((TextView) findViewById(R.id.tvAutoInfo)).setText(getString(
					airforce ? R.string.tvAutoOnAirForce : R.string.tvAutoOnArmy));
		// Ο τίτλος του προγράμματος
		setTitle(airforce ? R.string.app_name_airforce : R.string.app_name_army);
		// Αν είναι αεροπορία, δε χρειάζονται components για έργο ΜΧ
		findViewById(R.id.cbConstruction).setVisibility(airforce ? View.GONE : View.VISIBLE);
		// Η χρηματοδότηση είναι διαφορετική στο Στρατό και στην Αεροπορία
		adapter = new ArrayAdapter(
				MainActivity.this, android.R.layout.simple_spinner_item,
				getResources().getTextArray(
						airforce ? R.array.FinancingTypeAirforce : R.array.FinancingTypeArmy));
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		((Spinner) findViewById(R.id.spFinancingType)).setAdapter(adapter);
		// Τα είδη τιμολογίου, είναι διαφορετικά στο Στρατό και στην Αεροπορία
		adapter = new ArrayAdapter(
				MainActivity.this, android.R.layout.simple_spinner_item,
				getResources().getTextArray(
						airforce ? R.array.InvoiceTypeAirForce : R.array.InvoiceTypeArmy));
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		((Spinner) findViewById(R.id.spInvoiceType)).setAdapter(adapter);
		calculation();
	}

	/** Ο αυτόματος υπολογισμός είναι ενεργός.
	 * @return Ο αυτόματος υπολογισμός είναι ενεργός */
	private boolean isAuto() { return ((CompoundButton) findViewById(R.id.swAuto)).isChecked(); }

	/** Κοινός κώδικας που δημιουργεί τον ArrayAdapter του spinner του ΦΠΑ, κρατήσεων και ΦΕ.
	 * @param array Το array κρατήσεων, ΦΕ ή ΦΠΑ που θα περιέχει ο adapter
	 * @return Ο adapter */
	private ArrayAdapter createAdapter(Object[] array) {
		ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item,
				new ArrayList<>(Arrays.asList(array)));
		adapter.add("Άλλο");
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		return adapter;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_about) { // Menu "Σχετικά"
			new AlertDialog.Builder(this)
					.setTitle(R.string.about)
					.setMessage(String.format(getString(R.string.aboutInfo), BuildConfig.VERSION_CODE))
					.setNeutralButton(android.R.string.ok, null)
					.setCancelable(true)
					.create().show();
			return true;
		} else if (id == R.id.action_arm) { // Menu "Κλάδος ΕΔ"
			new AlertDialog.Builder(this)
					.setTitle(R.string.arm)
					.setPositiveButton(R.string.armArmy, (dialog, which) -> setMode(false))
					.setNeutralButton(R.string.armAirForce, (dialog, which) -> setMode(true))
					.show();
			return true;
		} else return super.onOptionsItemSelected(item);
	}

	/** Εμφανίζει πληροφορίες σε ένα TextView, σχετικές με το ποσοστό ΦΕ του τιμολογίου.
	 * Η κλήση εκτελείται όταν τροποποιούνται οι κρατήσεις, το ΦΠΑ ή το ΦΕ. Αν τροποποιείται το ΦΕ,
	 * για το νέο ΦΕ εμφανίζει πληροφορίες σχετικά με το ΦΕ. Σε κάθε περίπτωση, επανυπολογίζονται τα
	 * ποσά του τιμολογίου.
	 * @param s Το μενού στο οποίο άλλαξε η επιλογή. Είναι είτε το μενού κρατήσεων, είτε ΦΠΑ, είτε ΦΕ. */
	private void setIncomeTaxInfo(AdapterView s) {
		if (s == findViewById(R.id.spIncomeTax)) {
			String [] ar = getResources().getStringArray(R.array.IncomeTaxInfo);
			int sel = s.getSelectedItemPosition();
			String txt = sel < ar.length ? ar[sel] : "";
			((TextView) findViewById(R.id.tvFEInfo)).setText(txt);
		}
		calculation();
	}

	@Override
	public void onStop() {
		// Αποθήκευση των στοιχείων του παραθύρου
		SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
		edit.putInt(CONTRACTOR, ((Spinner) findViewById(R.id.spContractorType)).getSelectedItemPosition());
		edit.putInt(AMOUNT_TYPE, ((Spinner) findViewById(R.id.spAmountType)).getSelectedItemPosition());
		edit.putInt(INVOICE_TYPE, ((Spinner) findViewById(R.id.spInvoiceType)).getSelectedItemPosition());
		edit.putBoolean(ARM_TYPE, airforce);
		edit.putBoolean(AUTOMATIC, ((Switch) findViewById(R.id.swAuto)).isChecked());
		edit.putInt(FINANCING_TYPE, ((Spinner) findViewById(R.id.spFinancingType)).getSelectedItemPosition());
		edit.putBoolean(CONSTRUCTION, ((CheckBox) findViewById(R.id.cbConstruction)).isChecked());
		if (((Spinner) findViewById(R.id.spVATUnit)).getSelectedItemPosition() == 0)
			edit.putInt(VAT, (Integer) ((Spinner) findViewById(R.id.spVAT)).getSelectedItem());
		else {
			String s = ((EditText) findViewById(R.id.txtVat)).getText().toString();
			edit.putFloat(VAT, s.isEmpty() ? 0 : Float.parseFloat(s));
		}
		edit.putInt(INCOME_TAX, (Integer) ((Spinner) findViewById(R.id.spIncomeTax)).getSelectedItem());
		edit.putLong(HOLDS, Double.doubleToRawLongBits(((Hold) (((Spinner) findViewById(R.id.spHolds)).getSelectedItem())).total));
		String s = ((EditText) findViewById(R.id.txtAmount)).getText().toString();
		edit.putFloat(AMOUNT, s.isEmpty() ? 0 : Float.parseFloat(s));
		edit.apply();
		super.onStop();
	}

	/** Υπολογισμός της καθαρής αξίας όταν δίνεται καταλογιστέο, πληρωτέο ή υπόλοιπο πληρωτέο.
	 * @param contractor Ο τύπος του εκδότη του τιμολογίου
	 * @param amountType Ο τύπος του ποσού (καθαρή αξία, καταλογιστέο, πληρωτέο, υπόλοιπο πληρωτέο)
	 * @param amount Το ποσό
	 * @param fpa Το ποσοστό ΦΠΑ
	 * @param holds Το ποσοστό κρατήσεων
	 * @param fe Το ποσοστό ΦΕ
	 * @return Η καθαρή αξία του τιμολογίου */
	static private double calculateNetPercent(int contractor, int amountType, double amount, double fpa,
									   double holds, double fe) {
		switch(contractor) {
			case 0:	// Ιδιώτης
				switch(amountType) {
					case 1: amount /= 1.0 + fpa; break;	// Καταλογιστέο
					case 2: amount /= 1.0 + fpa - holds; break; // Πληρωτέο
					case 3: // Υπόλοιπο πληρωτέο
						// Στις εργολαβίες το ΦΕ υπολογίζεται επί της καθαρής αξίας, ειδάλλως επί της καθαρής αξίας μειον κρατήσεις
						double feFactor = 1.0 - (fe == 0.03 ? 0 : holds);
						amount /= 1.0 + fpa - holds - fe * feFactor;
					// default: Καθαρή αξία
				}
				break;
			case 1:	// ΝΠΔΔ
				switch(amountType) {
					case 1: amount /= 1.0 + fpa + holds; break;	// Καταλογιστέο
					case 2: case 3: amount /= 1.0 + fpa; // Πληρωτέο ή Υπόλοιπο πληρωτέο
					// default: Καθαρή αξία
				}
				break;
			default:	//case 2:	// Στρατός
				if (amountType == 1) amount /= 1.0 + holds;	// Καταλογιστέο
				// else: Καθαρή αξία ή Πληρωτέο ή Υπόλοιπο πληρωτέο
		}
		// Στρογγυλοποίηση
		return Math.round(amount * 100.0) / 100.0;
	}
	/** Υπολογισμός της καθαρής αξίας όταν δίνεται καταλογιστέο, πληρωτέο ή υπόλοιπο πληρωτέο.
	 * @param contractor Ο τύπος του εκδότη του τιμολογίου
	 * @param amountType Ο τύπος του ποσού (καθαρή αξία, καταλογιστέο, πληρωτέο, υπόλοιπο πληρωτέο)
	 * @param amount Το ποσό
	 * @param fpa Το ΦΠΑ του τιμολογίου σε ευρώ
	 * @param holds Το ποσοστό κρατήσεων
	 * @param fe Το ποσοστό ΦΕ
	 * @return Η καθαρή αξία του τιμολογίου */
	static private double calculateNetEuro(int contractor, int amountType, double amount, double fpa,
	                                   double holds, double fe) {
		switch(contractor) {
			case 0:	// Ιδιώτης
				switch(amountType) {
					case 1: amount -= fpa; break;	// Καταλογιστέο
					case 2: amount -= fpa; amount /= 1.0 - holds; break; // Πληρωτέο
					case 3: // Υπόλοιπο πληρωτέο
						// Στις εργολαβίες το ΦΕ υπολογίζεται επί της καθαρής αξίας, ειδάλλως επί της καθαρής αξίας μειον κρατήσεις
						double feFactor = 1.0 - (fe == 0.03 ? 0 : holds);
						amount -= fpa; amount /= 1.0 - holds - fe * feFactor;
					// default: Καθαρή αξία
				}
				break;
			case 1:	// ΝΠΔΔ
				switch(amountType) {
					case 1: amount -= fpa; amount /= 1.0 + holds; break;	// Καταλογιστέο
					case 2: case 3: amount -= fpa; // Πληρωτέο ή Υπόλοιπο πληρωτέο
					// default: Καθαρή αξία
				}
				break;
			default:	//case 2:	// Στρατός
				if (amountType == 1) amount /= 1.0 + holds;	// Καταλογιστέο
				// else: Καθαρή αξία ή Πληρωτέο ή Υπόλοιπο πληρωτέο
		}
		// Στρογγυλοποίηση
		return Math.round(amount * 100.0) / 100.0;
	}

	/** Αυτοματοποιημένος υπολογισμός ποσοστού κρατήσεων του τιμολογίου για το Στρατό.
	 * @param contractor Ο τύπος του εκδότη του τιμολογίου
	 * @param invoiceType Ο τύπος του τιμολογίου
	 * @param financing Ο τύπος της χρηματοδότησης
	 * @param net Η καθαρή αξία του τιμολογίου
	 * @return Οι κρατήσεις του τιμολογίου */
	static private Hold calculateHoldArmy(int contractor, int invoiceType, int financing, double net) {
		int idx;
		if (invoiceType == 4 /*Λογαριασμοί νερού/ΔΕΗ*/) idx = 10;
		else {
			idx = 5 * financing;
			if (contractor != 0 /*1:ΝΠΔΔ 2:Στρατος*/) idx += 4;
			else {
				if (net >= PRICE_HOLD_CONTRACT && invoiceType != 3/*Μισθώματα ακινήτων*/) idx += 2;
				if (invoiceType == 5 || invoiceType == 6) ++idx;
			}
		}
		return holdListArmy[idx];
	}

	/** Αυτοματοποιημένος υπολογισμός ποσοστού κρατήσεων του τιμολογίου για την Αεροπορία.
	 * @param invoiceType Ο τύπος του τιμολογίου
	 * @param financing Ο τύπος της χρηματοδότησης
	 * @param net Η καθαρή αξία του τιμολογίου
	 * @return Οι κρατήσεις του τιμολογίου */
	static private Hold calculateHoldAirForce(int invoiceType, int financing, double net) {
		Hold hold;
		if (invoiceType == 4 /*Λογαριασμοί νερού/ΔΕΗ*/) hold = holdListAirForce[0];
		else if (net >= PRICE_HOLD_CONTRACT && invoiceType != 3 /*Μισθώματα ακινήτων*/)
			hold = holdListAirForce[financing == 0 /*Τακτικός Π/Υ*/ ? 3 : 1];
		else
			hold = holdListAirForce[financing == 0 /*Τακτικός Π/Υ*/ ? 2 : 0];
		return hold;
	}

	/** Αυτοματοποιημένος υπολογισμός ποσοστού κρατήσεων του τιμολογίου.
	 * @param contractor Ο τύπος του εκδότη του τιμολογίου
	 * @param invoiceType Ο τύπος του τιμολογίου
	 * @param financing Ο τύπος της χρηματοδότησης
	 * @param net Η καθαρή αξία του τιμολογίου
	 * @return Οι κρατήσεις του τιμολογίου */
	private Hold calculateHold(int contractor, int invoiceType, int financing, double net) {
		return airforce ? calculateHoldAirForce(invoiceType, financing, net)
				: calculateHoldArmy(contractor, invoiceType, financing, net);
	}

	/** Η καθαρή αξία πάνω από την οποία έχουμε σύμβαση. */
	static final private int PRICE_CONTRACT = 2500;
	/** Η καθαρή αξία πάνω από την οποία οι κρατήσεις προσαυξάνονται. */
	static final private int PRICE_HOLD_CONTRACT = 1000;
	/** Η καθαρή αξία κάτω από την οποία δεν έχουμε ΦΕ. */
	static final private int PRICE_INCOME_TAX = 150;

	/** Το υπολογιστικό τμήμα του προγράμματος. */
	private void calculation() {
		try {
			// Λήψη όλων των απαραίτητων δεδομένων για τους υπολογισμούς
			int contractor = ((Spinner) findViewById(R.id.spContractorType)).getSelectedItemPosition();
			int amountType = ((Spinner) findViewById(R.id.spAmountType)).getSelectedItemPosition();
			int invoiceType = ((Spinner) findViewById(R.id.spInvoiceType)).getSelectedItemPosition();
			boolean auto = isAuto();
			CompoundButton cbConstruction = findViewById(R.id.cbConstruction);
			boolean construction = cbConstruction.isChecked();
			double amount = Double.parseDouble(((EditText) (findViewById(R.id.txtAmount))).getText().toString());
			boolean fpaUnit = ((Spinner) findViewById(R.id.spVATUnit)).getSelectedItemPosition() != 0;
			double fpaPercent = 0, fpa = 0;
			if (fpaUnit) fpa = Double.parseDouble(((EditText) findViewById(R.id.txtVat)).getText().toString());
			else fpaPercent = Double.parseDouble(((Spinner) findViewById(R.id.spVAT)).getSelectedItem().toString()) / 100.0;
			double fePercent;
			Hold hold;

			// Αυτόματη εύρεση κρατήσεων - ΦΕ
			// Το πρόβλημα εδώ είναι ότι δεν ξέρουμε την καθαρή αξία!
			// Για το λόγο αυτό κάποιοι έλεγχοι θα γίνουν μεταγενέστερα.
			if (auto) {
				Spinner spFinancingType = findViewById(R.id.spFinancingType);
				int financing = spFinancingType.getSelectedItemPosition();

				// Περιορισμοί
				// Σε δαπάνες Π/Υ ΠΔΕ, προμηθευτής είναι πάντα ιδιώτης
				if (contractor != 0 /*Όχι ιδιώτης*/ && financing == 2 /*Π/Υ ΠΔΕ*/)
					spFinancingType.setSelection(financing = 0);
				// Σε κατασκευαστικές δαπάνες, προμηθευτής είναι πάντα ιδιώτης
				if (contractor != 0 /*Όχι ιδιώτης*/) construction = false;
				// Σε τιμολόγιο αεροπορίας
				if (airforce) {
					construction = false;               // δεν έχουμε κατασκευαστικές δαπάνες
					if (contractor != 0 /*Ιδιώτης*/)	// και προμηθευτής είναι πάντα ιδιώτης
						((Spinner) findViewById(R.id.spContractorType)).setSelection(contractor = 0);
				}
				// Σε κατασκευαστικές δαπάνες, τιμολόγια προμήθειας υλικών, παροχής υπηρεσιών ή εκπόνησης μελετών μόνο
				if (construction && invoiceType != 0 /*Προμήθεια υλικών*/ && invoiceType != 1 /*Παροχή υπηρεσιών*/
						&& invoiceType < 5 /*5:Εκπόνηση & 6:Επίβλεψη μελετών*/)
					cbConstruction.setChecked(construction = false);
				// Σε δαπάνες που προμηθευτής είναι ο Στρατός, τα τιμολόγια είναι πάντα προμήθειας υλικών
				if (contractor == 2 /*Στρατός*/ && invoiceType != 0 /*Προμήθεια υλικών*/)
					invoiceType = 0; //Προμήθεια υλικών

				// Υπολογισμός του ΦΕ
				if (contractor != 0 /*Όχι ιδιώτης*/ || invoiceType == 3 /*Μίσθωση ακινήτου*/
						|| invoiceType == 4 /*Λογαριασμοί νερού - έργα ΔΕΗ*/) fePercent = 0;
				// Μεταγενέστερα: if (!construction && net <= 150) fePercent = 0;
				else if (invoiceType == 2 /*Προμήθεια υγρών καυσίμων*/) fePercent = 0.01;
				else if (invoiceType == 1 /*Παροχή υπηρεσιών*/)
					fePercent = construction /*Κατασκευή έργου*/ ? 0.03 : 0.08;
				else if (invoiceType == 6 /*Επίβλεψη μελέτης*/) fePercent = 0.1;
				else fePercent = 0.04; /*invoiceType: 0:Προμήθεια υλικών ή 5:Εκπόνηση μελέτης*/

				// Υπολογισμός κρατήσεων, αρχικά θεωρώντας την καθαρή αξία >= PRICE_HOLD_CONTRACT
				// To +0.001 προστίθεται για να αποφευχθεί κάποια καταστροφική αποκοπή
				hold = calculateHold(contractor, invoiceType, financing, PRICE_HOLD_CONTRACT + 0.001);
				// Εύρεση καθαρής αξίας
				double net = fpaUnit
						? calculateNetEuro(contractor, amountType, amount, fpa, hold.total, 0)
						: calculateNetPercent(contractor, amountType, amount, fpaPercent, hold.total, 0);
				// Αν καθαρή αξία < PRICE_HOLD_CONTRACT, επανυπολογισμός κρατήσεων και καθαρής αξίας
				if (net < PRICE_HOLD_CONTRACT) {
					hold = calculateHold(contractor, invoiceType, financing, net);
					net = fpaUnit
							? calculateNetEuro(contractor, amountType, amount, fpa, hold.total, 0)
							: calculateNetPercent(contractor, amountType, amount, fpaPercent, hold.total, 0);
				}
				// Αν καθαρή αξία <= PRICE_INCOME_TAX και δεν έχουμε κατασκευή, το ΦΕ είναι 0
				if (!construction && net <= PRICE_INCOME_TAX) fePercent = 0;
			} else {
				// Εύρεση κρατήσεων και ΦΕ
				hold = (Hold) ((Spinner) findViewById(R.id.spHolds)).getSelectedItem();
				fePercent = Double.parseDouble(((Spinner) findViewById(R.id.spIncomeTax)).getSelectedItem().toString()) / 100.0;
			}
			// Εύρεση καθαρής αξίας
			amount = fpaUnit
					? calculateNetEuro(contractor, amountType, amount, fpa, hold.total, fePercent)
					: calculateNetPercent(contractor, amountType, amount, fpaPercent, hold.total, fePercent);
			// Υπολογισμοί ενδιάμεσων τιμών
			if (!fpaUnit) fpa = Math.round(amount * fpaPercent * 100.0) / 100.0;
			double holds = Math.round(amount * hold.total * 100.0) / 100.0;
			double mixed = amount;
			if (contractor != 2) mixed += fpa;		// όχι Στρατός
			if (contractor != 0) mixed += holds;	// ΝΠΔΔ, Στρατός
			double final1 = mixed - holds;
			double fe = amount - (fePercent == 0.03 ? 0 : holds);
			fe = Math.round(fe * fePercent * 100.0) / 100.0;
			double final2 = final1 - fe;
			// Εξαγωγή αποτελεσμάτων
			DecimalFormat df = new DecimalFormat("0.#####%");	// ποσοστά
			DecimalFormat df2 = new DecimalFormat("0.00¤");	// νομισματικά
			StringBuilder txt = new StringBuilder(4096);
			txt.append(String.format(getString(R.string.resNet), df2.format(amount)));
			String txtHolds = String.format(getString(R.string.resHolds),
					df.format(hold.total), df2.format(holds));
			if (contractor != 0 /* ΝΠΔΔ, Στρατός, Ύδρευση-Έργα ΔΕΗ */) txt.append("+ ").append(txtHolds);
			if (contractor != 2 /* όχι Στρατός */)
				if (fpaUnit) txt.append(String.format(getString(R.string.resVAT2), df2.format(fpa)));
				else txt.append(String.format(getString(R.string.resVAT1),
							df.format(fpaPercent), df2.format(fpa)));
			txt.append(String.format(getString(R.string.resMixed),
					df2.format(mixed), txtHolds, df2.format(final1)));
			if (contractor == 0 /* Ιδιώτης */ && fePercent > 0)
				txt.append(String.format(getString(R.string.resIncomeTax),
						df.format(fePercent), df2.format(fe), df2.format(final2)));
			((TextView) findViewById(R.id.tvResults)).setText(txt);
			// Εξαγωγή απαιτούμενων
			txt = new StringBuilder(4096);
			if (contractor == 0) { // Ιδιώτης
				if (mixed > 1500) txt.append(getString(R.string.reqTaxCurrency)).append("\n");
				if (mixed > 3000) txt.append(getString(R.string.reqInsuranceCurrency)).append("\n");
				//if (amount > 2500) txt.append(getString(R.string.reqInsuranceCurrency)).append("\n");
				if (amount > PRICE_CONTRACT) txt.append(getString(R.string.reqCriminalRecord)).append("\n");
			}
			if (contractor != 2 /* όχι Στρατός */ && (amount > PRICE_CONTRACT || construction && auto))
				txt.append(getString(R.string.reqContract)).append("\n");
			if (amount > 60000) txt.append(getString(R.string.reqCompetition)).append("\n");
			else if (amount > 30000)
				if (!auto) txt.append(getString(R.string.reqCompetitionManual)).append("\n");
				else if (!construction) txt.append(getString(R.string.reqCompetitionSocial)).append("\n");
			if (amount > 20000) txt.append(getString(R.string.reqCriminalRecordDenial)).append("\n");
			if (auto && construction && invoiceType == 1 /* Παροχή υπηρεσιών */)
				txt.append(String.format(getString(R.string.reqConstructionContractor),
						df2.format(amount * 0.01), df2.format(amount * 0.006), df2.format(amount * 0.0025)))
						.append("\n");
			if (txt.length() == 0) txt.append(getString(R.string.reqEmpty)).append("\n");
			((TextView) findViewById(R.id.tvRequirements)).setText(txt.substring(0, txt.length() - 1));	// remove last newline
			// Ανάλυση κρατήσεων
			txt = new StringBuilder(4096);
			if (hold.data != null) {
				double[] holdsAll = hold.euro(holds);
				String[] txtHoldsAll = getResources().getStringArray(airforce ? R.array.hldPartsAirForce : R.array.hldPartsArmy);
				String str = getString(R.string.hldPart);
				txt.append(String.format(str, getString(R.string.hldTotal), df.format(hold.total),
						df2.format(holds)));
				for (int z = 0; z < holdsAll.length; ++z)
					if (z >= hold.data.length) break;
					else if (hold.data[z] != 0)
						txt.append(String.format(str, txtHoldsAll[z], df.format(hold.data[z]),
								df2.format(holdsAll[z])));
			} else txt.append(getString(R.string.hldFail));
			((TextView) findViewById(R.id.tvHoldAnalysis)).setText(txt);
			// Εμφάνιση των widgets που είναι κρυφά όσο δεν υπάρχει αποτέλεσμα
			findViewById(R.id.layOut).setVisibility(View.VISIBLE);
		} catch (NumberFormatException e) {
			findViewById(R.id.layOut).setVisibility(View.GONE);
		}
	}

	/** Κράτηση. */
	static private class Hold {
		/** Δημιουργία μιας κράτησης, σαν σύνολο, χωρίς επιμέρους κρατήσεις.
		 * @param sum Το σύνολο της κράτησης */
		Hold(double sum) { data = null; total = sum; }
		/** Δημιουργία μιας κράτησης, με τις επιμέρους κρατήσεις.
		 * @param holds Οι επιμέρους κρατήσεις, χωρίς το σύνολο (που υπολογίζεται), με τη σειρά που
		 * εμφανίζονται τα ονόματά τους στο string array */
		Hold(double[] holds) {
			data = holds;
			double sum = 0;
			for (double item : data) sum += item;
			total = sum;
		}
		@Override public String toString() { return Double.toString(Math.round(total * 10000000.0)/ 100000.0).replaceAll("\\.0$", ""); }
		@Override public boolean equals(Object o) {
			if (o instanceof Hold) return ((Hold) o).total == total;
			return o instanceof Number && ((Number) o).doubleValue() == total;
		}
		/** Οι επιμέρους κρατήσεις σε €.
		 * @param holds Το συνολικό ποσό των κρατήσεων σε €
		 * @return Οι επιμέρους κρατήσεις σε €, διορθωμένες ώστε να έχουν άθροισμα το συνολικό ποσό */
		double[] euro(double holds) {
			if (data == null) return null;
			double euroTotal = 0;
			double[] euroData = new double[data.length];
			class Pair {
				private Pair(double remainder, int id) { this.remainder = remainder; this.id = id; }
				final private double remainder;
				final private int id;
			}
			ArrayList<Pair> remainders = new ArrayList<>();
			for (int z = 0; z < data.length; ++z) {
				double t = holds * data[z] / total;
				remainders.add(new Pair(Math.round(t) - t, z));
				euroTotal += euroData[z] = Math.round(t * 100.0) / 100.0;
			}

			// Σφάλματα στρογγυλοποίησης
			int remainder = (int) Math.round((euroTotal - holds) * 100.0);
			if (remainder != 0) {
				Collections.sort(remainders, (a, b) -> Double.compare(a.remainder, b.remainder));
				if (remainder > 0)
					for (int z = 0; z < remainder; ++z) {
						Pair p = remainders.get(remainders.size() - 1 - z);
						euroData[p.id] -= 0.01;
					}
				else
					for (int z = 0; z < -remainder; ++z) {
						Pair p = remainders.get(z);
						euroData[p.id] += 0.01;
					}
			}
			return euroData;
		}
		/** Το συνολικό ποσοστό των κρατήσεων. */
		final double total;
		/** Τα ποσοστά των επιμέρους κρατήσεων. Το άθροισμά τους είναι ίσο με το συνολικό ποσοστό. */
		final private double[] data;
	}
}
