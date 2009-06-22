#!/usr/bin/perl

use strict;
use POSIX qw(ceil floor);

my $snp = $ARGV[0];
my $chr = $ARGV[1];
my $collection = $ARGV[2];
my $index = $ARGV[3];
my $numinds = $ARGV[4];

#generate the .bed file for just this SNP.
my $bytesPerRecord = ceil($numinds/4);
open (BED, "$collection.$chr.bed");
open (BEDOUT, ">$collection.$snp.bed");

my $magic_num;
#magic number and SNP-major mode.
read(BED, $magic_num, 3);

my $buf;
#jump to this SNP (the +3 is for the meta-data, as above)
seek (BED, ($index*$bytesPerRecord)+3,0);
read (BED, $buf, $bytesPerRecord);
print BEDOUT $buf;

close BED;
close BEDOUT;

#generate the .bnt file for just this SNP.
$bytesPerRecord = $numinds*8;
open (BNT, "$collection.$chr.bnt");
open (BNTOUT, ">$collection.$snp.bnt");

read (BNT, $magic_num, 2);
print BNTOUT $magic_num;

#jump to position (+2 for meta-data)
seek (BNT, ($index*$bytesPerRecord)+2,0);
read (BNT, $buf, $bytesPerRecord);	
print BNTOUT $buf;

close BNT;
close BNTOUT;

print "$snp\n";
