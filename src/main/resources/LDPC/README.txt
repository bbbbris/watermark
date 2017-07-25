---------------------------------------------------
Simulator for Quasi-Cyclic LDPC Codes as specified
in the IEEE 802.11n WLAN standard
---------------------------------------------------
(c) Dr. Christoph Studer 2011 (studer@rice.edu)
---------------------------------------------------

# Important information:

If you are thinking of contacting us, please do not e-mail the author to ask for download instructions, installation guidelines, or the toolbox itself. Note that we will NOT help to debug user-generated code that is not included in the provided package. If, however, you notice a bug in our code, please be so kind to contact the author of this software: C. Studer (studer@rice.edu). 

The package is supplied "as is", without any accompanying support services, maintenance, or future updates. We make no warranties, explicit or implicit, that the software contained in this package is free of error or that it will meet your requirements for any particular application. It should not be relied on for any purpose where incorrect results could result in loss of property, personal injury, liability or whatsoever. Remember: If you do use our software for any such purpose, it is at your own risk. The authors disclaim all liability of any kind, either direct or consequential, resulting from your use of these programs.

# How to start a simulation:

Note that Matlab must be started in the main folder in order to automatically include all paths defined in the userpath.m file. Note that the necessary paths can also be included manually (using the Matlab GUI). Starting a simulation is straightforward: The simulator bases on parameter files (found in the param/ folder), which are used to define all necessary simulation settings and also start the LDPC encoding and decoding simulations. For example, type

>> ERR_LDPC_648b_R12_LAYERED_OMS_I5(0)

which starts a simulation with a QC-LDPC code in an AWGN channel using the code matrix of size 648 and a rate 1/2. The decoder algorithm performs the layered schedule and uses offset-min-sum (OMS) message computation. The value 0 determines the random seed and is useful in combination with high-throughput computing, e.g., in combination with Condor. 

The folder "codes" contains all codes specified in IEEE 802.11n. For each QC-LDPC code, there exists a Matlab script describing the code properties, e.g., the file LDPC_11nD2_648b_R12.m contains all the essential information about the length-648 rate-1/2 code. Before a simulation with a new code can be used, one needs to execute this script (which calls genmat.m) in the codes/ folder, which results in a corresponding .mat file, e.g., LDPC_11nD2_648b_R12.mat (also located in the codes/ folder). This .mat file is then used by the parameter files and used by the simulator.

Important: We highly recommend you to execute the code step-by-step (using Matlab's debug mode) in order to gain some understanding of the simulator and how LDPC codes are described, constructed, and stored. 

# Version 1.0 (Dec 11, 2011) - initial version for public access